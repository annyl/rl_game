from gym_checkers.checkers import Checkers
import agent
import numpy as np

if __name__ == '__main__':
    env = Checkers()
    brain = agent.Agent(gamma=0.99, epsilon=1.0, batch_size=64,
                        n_actions=4, input_dims=[8], lr=0.003)
    scores = []
    eps_history = []
    n_games = 500
    score = 0

    for i in range(n_games):
        print(f"episode={i}, score={score}")
        if i % 10 == 0 and i > 0:
            avg_score = np.mean(scores[max(0, i-10): i+1])
            print(f"\taverage_score={avg_score:%3f}, eps={brain.epsilon:%3f}")
        score = 0
        eps_history.append(brain.epsilon)
        print(f"board = {env.flat_board()}")
        raise
