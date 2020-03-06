import torch
from agent import Agent
from tqdm import tqdm
from checkers import Checkers


def get_score(board: dict, turn: str):
    return len(board[turn]['men']) + len(board[turn]['kings']) * 2

def eval(agent:Agent, env: Checkers, color:str, n_games=100):
    agent.net.eval()
    opponent = Agent(gamma=agent.gamma,
                     epsilon=1,
                     lr=0,
                     input_dims=[8*8 + 1],
                     batch_size=agent.batch_size,
                     action_space=agent.action_space,
                     eps_dec=0,
                     max_mem_size=0
                     )
    opponent.net.eval()
    initial_state = env.save_state()
    score = {'black': 0, 'white': 0}

    for i in tqdm(range(n_games)):
        env.restore_state(initial_state)
        winner = None
        moves = torch.tensor(env.legal_moves())
        board, turn, last_moved_piece = env.save_state()
        brain = agent if turn == color else opponent
        board_tensor = torch.from_numpy(env.flat_board()).view(-1).float()
        encoded_turn = torch.tensor([1.]) if turn == 'black' else torch.tensor([0.])
        observation = torch.cat([board_tensor, encoded_turn])
        while not winner:
            action = brain.choose_action(observation, moves)
            new_board, new_turn, _, moves, winner = env.move(*action.tolist())
            moves = torch.tensor(moves)
            board_tensor = torch.from_numpy(env.flat_board()).view(-1).float()
            encoded_turn = torch.tensor([1. if turn == 'black' else 0.])
            observation = torch.cat([board_tensor, encoded_turn])
            brain = agent if turn == color else opponent
        score[winner] +=1
    agent.net.train()
    return score[color] / n_games


