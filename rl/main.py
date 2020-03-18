from checkers import Checkers
from agent import Agent, MobileAgent
import numpy as np
import torch
from argparse import ArgumentParser
import os


def get_score(board: dict, turn: str):
    return len(board[turn]['men']) + len(board[turn]['kings']) * 2

def generate_action_space():
    for x in range(32):
        increase_fwd_step = x // 4 % 2 == 0
        for forward_step in [3, 4]:
            yield (x, x + (forward_step + 1 if increase_fwd_step else forward_step))
        for backward_step in [3, 4]:
            yield (x, x - (backward_step if increase_fwd_step else backward_step + 1))
        for jump in [7, 9]:
            yield (x, x + jump)
            yield (x, x - jump)



if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('--games', type=int, default=500)
    parser.add_argument('--lr', type=int, default=3e-3)
    parser.add_argument('--batch-size', type=int, default=64)
    parser.add_argument('--gamma', type=float, default=0.99)
    parser.add_argument('--epsilon', type=float, default=1.0)
    parser.add_argument('--epsilon-decay', type=float, default=0.9996)
    parser.add_argument('--checkpoints-dir', type=str,
                        default='../checkpoints')
    parser.add_argument('--save_every', type=int, default=100)
    parser.add_argument('--save_dir', type=str, default='')
    args = parser.parse_args()

    action_space = list(generate_action_space())
    print(action_space)
    players = {'black':
               Agent(gamma=args.gamma,
                     epsilon=args.epsilon,
                     batch_size=args.batch_size,
                     action_space=action_space,
                     input_dims=[8*8+1],
                     lr=args.lr,
                     eps_dec=args.epsilon_decay),
               'white':
               Agent(gamma=args.gamma,
                     epsilon=args.epsilon,
                     batch_size=args.batch_size,
                     action_space=action_space,
                     input_dims=[8*8+1],
                     lr=args.lr,
                     eps_dec=args.epsilon_decay)}
    env = Checkers()
    initial_state = env.save_state()
    scores = {'black': [], 'white': []}
    eps_history = []
    score = {'black': 0, 'white': 0}

    os.makedirs(args.checkpoints_dir, exist_ok=True)

    for i in range(args.games):
        print(f"episode={i}, score={score}")
        if i % 10 == 0 and i > 0:
            avg_score = {'black': np.mean(scores['black'][max(0, i-10): i+1]),
                         'white': np.mean(scores['white'][max(0, i-10): i+1])}
            print(
                f"\taverage_score={avg_score}",
                f'black_eps={players["black"].epsilon:.3f}',
                f'white_eps={players["white"].epsilon:.3f}')
        score = {'black': 0, 'white': 0}
        env.restore_state(initial_state)
        winner = None
        moves = torch.tensor(env.legal_moves())
        board, turn, last_moved_piece = env.save_state()
        brain = players[turn]
        board_tensor = torch.from_numpy(env.flat_board()).view(-1).float()
        encoded_turn = torch.tensor([1.]) if turn == 'black' else torch.tensor([0.])
        observation = torch.cat([board_tensor, encoded_turn])
        while not winner:
            action = brain.choose_action(observation, moves)
            new_board, new_turn, _, moves, winner = env.move(*action.tolist())
            moves=torch.tensor(moves)
            turn_score = get_score(new_board, turn) - get_score(board, turn)
            # print(f'{turn} score = {turn_score}')
            new_turn_score = get_score(
                new_board, new_turn) - get_score(board, new_turn)
            board, turn, _ = env.save_state()
            reward = turn_score - new_turn_score
            score[turn] += reward
            board_tensor = torch.from_numpy(env.flat_board()).view(-1).float()
            encoded_turn = torch.tensor([1. if turn == 'black' else 0.])
            new_observation = torch.cat([board_tensor, encoded_turn])
            brain.store_transition(observation, action,
                                   reward, new_observation, bool(winner))
            brain.learn()
            observation = new_observation
            brain = players[turn]

        for key in players.keys():
            scores[key].append(score[key])

        if i % args.save_every == 0:
            for key, agent in players.items():
                agent.net.eval()
                m_agent = MobileAgent(agent)
                path = os.path.join(args.checkpoints_dir, f'{key}[{i + 1}].pt')
                torch.jit.script(m_agent).save(path)
                agent.net.train()