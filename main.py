from checkers import Checkers
from agent import Agent
import numpy as np
import torch
from time import time
from argparse import ArgumentParser
import os


def get_score(board: dict, turn: str):
    return len(board[turn]['men']) + len(board[turn]['kings']) * 2


if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('--games', type=int, default=500)
    parser.add_argument('--lr', type=int, default=500)
    parser.add_argument('--batch-size', type=int, default=64)
    parser.add_argument('--gamma', type=float, default=0.99)
    parser.add_argument('--epsilon', type=float, default=1.0)
    parser.add_argument('--epsilon-decay', type=float, default=0.996)
    parser.add_argument('--checkpoints-dir', type=str,
                        default='../checkpoints')
    args = parser.parse_args()

    action_space = [(x, y) for x in range(32)
                    for y in range(32) if x != y]
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
                     batch_size=64,
                     action_space=action_space,
                     input_dims=[8*8+1],
                     lr=args.lr,
                     eps_dec=args.epsilon_decay)}
    env = Checkers()
    initial_state = env.save_state()
    scores = {'black': [], 'white': []}
    eps_history = []
    score = {'black': 0, 'white': 0}

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
        moves = env.legal_moves()
        board, turn, last_moved_piece = env.save_state()
        brain = players[turn]
        board_tensor = np.array(env.flat_board()).flatten()
        encoded_turn = 1 if turn == 'black' else 0
        observation = np.append(board_tensor, encoded_turn)
        while not winner:
            # env.print_board()
            action = brain.choose_action(observation, moves)
            new_board, new_turn, _, moves, winner = env.move(*action)
            turn_score = get_score(new_board, turn) - get_score(board, turn)
            # print(f'{turn} score = {turn_score}')
            new_turn_score = get_score(
                new_board, new_turn) - get_score(board, new_turn)
            board, turn, _ = env.save_state()
            reward = turn_score - new_turn_score
            score[turn] += reward
            board_tensor = np.array(env.flat_board()).flatten()
            encoded_turn = 1 if turn == 'black' else 0
            new_observation = np.append(board_tensor, encoded_turn)
            brain.store_transition(observation, action,
                                   reward, new_observation, bool(winner))
            brain.learn()
            observation = new_observation
            brain = players[turn]
        scores['black'].append(score['black'])
        scores['white'].append(score['white'])

    os.makedirs(args.checkpoints_dir, exist_ok=True)
    torch.save(players['black'].net.state_dict(), os.path.join(
        args.checkpoints_dir, f'black_{time()}.pt'))
    torch.save(players['white'].net.state_dict(), os.path.join(
        args.checkpoints_dir, f'white_{time()}.pt'))
