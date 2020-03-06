import torch
from agent import Agent
from checkers import Checkers
from tqdm import tqdm

def eval(agent:Agent, color:str, num_games: int = 100):
    opponent = Agent(gamma=agent.gamma,
                     epsilon=agent.epsilon,
                     batch_size=agent.batch_size,
                     action_space=agent.action_space,
                     input_dims=[8*8+1],
                     lr=0,
                     eps_dec=agent.eps_dec)

    env = Checkers()
    initial_state = env.save_state()
    agent.net.eval()
    opponent.net.eval()
    scores = {'black':0, 'white': 0}

    for _ in tqdm(range(num_games)):
        env.restore_state(initial_state)
        winner = None
        moves = torch.tensor(env.legal_moves())
        board, turn, last_moved_piece = env.save_state()
        brain = agent if color == turn else opponent
        board_tensor = torch.from_numpy(env.flat_board()).view(-1).float()
        encoded_turn = torch.tensor([1.]) if turn == 'black' else torch.tensor([0.])
        observation = torch.cat([board_tensor, encoded_turn])
        while not winner:
            action = brain.choose_action(observation, moves)
            new_board, new_turn, _, moves, winner = env.move(*action.tolist())
            moves = torch.tensor(moves)
            turn_score = get_score(new_board, turn) - get_score(board, turn)
            # print(f'{turn} score = {turn_score}')
            new_turn_score = get_score(
                new_board, new_turn) - get_score(board, new_turn)
            board, turn, _ = env.save_state()
            reward = turn_score - new_turn_score
            board_tensor = torch.from_numpy(env.flat_board()).view(-1).float()
            encoded_turn = torch.tensor([1. if turn == 'black' else 0.])
            new_observation = torch.cat([board_tensor, encoded_turn])
            observation = new_observation
            brain = agent if color == turn else opponent
        scores[winner] += 1

    return scores[color] / (sum(scores.values()))

def get_score(board: dict, turn: str):
    return len(board[turn]['men']) + len(board[turn]['kings']) * 2
