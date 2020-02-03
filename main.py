from checkers import Checkers
from agent import Agent
import numpy as np
import torch
from time import time


def get_score(board: dict, turn: str):
    #print(f'{len(board[turn]["men"])} {turn} men + {len(board[turn]["kings"])} {turn} kings = {len(board[turn]["men"]) + len(board[turn]["kings"]) * 2} score')
    return len(board[turn]['men']) + len(board[turn]['kings']) * 2


if __name__ == '__main__':
    action_space = [(x, y) for x in range(32)
                    for y in range(32) if x != y]
    players = {'black': Agent(gamma=0.99, epsilon=1.0, batch_size=64,
                              action_space=action_space, input_dims=[8*8+1], lr=0.003),
               'white': Agent(gamma=0.99, epsilon=1.0, batch_size=64,
                              action_space=action_space, input_dims=[8*8+1], lr=0.003)}
    env = Checkers()
    initial_state = env.save_state()
    scores = {'black': [], 'white': []}
    eps_history = []
    n_games = 500
    score = 0

    for i in range(n_games):
        print(f"episode={i}, score={score}")
        if i % 10 == 0 and i > 0:
            avg_score = {'black': np.mean(scores['black'][max(0, i-10): i+1]),
                         'white': np.mean(scores['white'][max(0, i-10): i+1])}
            print(
                f"\taverage_score={avg_score}, black_eps={players['black'].epsilon:.3f}, white_eps={players['white'].epsilon:.3f}")
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

    torch.save(players['black'].net.state_dict(),
               f'../checkpoints/black_{time()}.pt')
