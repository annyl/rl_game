import numpy as np
import torch
from torch import nn
from torch import optim
import torch.nn.functional as F


class DeepQNetwork(nn.Module):
    def __init__(self, lr, input_dims, fc1_dims, fc2_dims, n_actions):
        super(DeepQNetwork, self).__init__()
        self.fc1 = nn.Linear(*input_dims, fc1_dims)
        self.fc2 = nn.Linear(fc1_dims, fc2_dims)
        self.fc3 = nn.Linear(fc2_dims, n_actions)
        self.optimizer = optim.Adam(self.parameters(), lr=lr)
        self.loss = nn.MSELoss()
        self.device = torch.device(
            "cuda" if torch.cuda.is_available() else "cpu")
        self.to(self.device)

    def forward(self, observation):
        state = torch.Tensor(observation).to(self.device)
        x = F.relu(self.fc1(state))
        x = F.relu(self.fc2(x))
        actions = self.fc3(x)
        return actions


class Agent:
    def __init__(self, gamma, epsilon, lr, input_dims,
                 batch_size, action_space, max_mem_size=1000000,
                 eps_min=0.01, eps_dec=0.996):
        self.n_actions = len(action_space)
        self.epsilon = epsilon
        self.batch_size = batch_size
        self.gamma = gamma
        self.action_space = {action: i for i,
                             action in enumerate(action_space)}
        self.net = DeepQNetwork(lr, input_dims, 256, 256, self.n_actions)
        self.mem_size = max_mem_size
        self.mem_cntr = 0
        self.state_memory = np.zeros((max_mem_size, *input_dims))
        self.new_state_memory = np.zeros((max_mem_size, *input_dims))
        self.action_memory = np.zeros(
            (max_mem_size, self.n_actions), dtype=np.uint8)
        self.reward_memory = np.zeros(max_mem_size)
        self.terminal_memory = np.zeros(max_mem_size, dtype=np.uint8)
        self.eps_dec = eps_dec
        self.eps_min = eps_min

    def store_transition(self, state, action, reward, new_state, terminal):
        index = self.mem_cntr % self.mem_size
        self.state_memory[index] = state
        actions = np.zeros(self.n_actions)
        actions[self.action_space[action]] = 1.0
        self.action_memory[index] = actions
        self.reward_memory[index] = reward
        self.terminal_memory[index] = 1 - terminal
        self.new_state_memory[index] = new_state
        self.mem_cntr += 1

    def choose_action(self, observation, legal_actions):
        if not legal_actions:
            return 0
        rand = np.random.random()
        if rand < self.epsilon:
            return legal_actions[np.random.randint(len(legal_actions))]
        actions = self.net.forward(observation)
        legal_actions_idx = [self.action_space[legal_action]
                             for legal_action in legal_actions]
        action = torch.argmax(actions[legal_actions_idx]).item()
        return list(self.action_space.keys())[legal_actions_idx[action]]

    def learn(self):
        if self.mem_cntr > self.batch_size:
            self.net.optimizer.zero_grad()
        max_mem = min(self.mem_cntr, self.mem_size)
        batch = np.random.choice(max_mem, self.batch_size)
        state_batch = self.state_memory[batch]
        action_batch = self.action_memory[batch]
        action_values = [self.action_space[action]
                         for action in self.action_space]
        action_indices = np.dot(action_batch, action_values)
        reward_batch = self.reward_memory[batch]
        terminal_batch = self.terminal_memory[batch]
        new_state_batch = self.new_state_memory[batch]

        reward_batch = torch.Tensor(reward_batch).to(self.net.device)
        terminal_batch = torch.Tensor(terminal_batch).to(self.net.device)
        q = self.net.forward(state_batch).to(self.net.device)
        q_target = self.net.forward(state_batch).to(self.net.device)
        q_next = self.net.forward(new_state_batch).to(self.net.device)
        batch_index = np.arange(self.batch_size, dtype=np.int32)
        q_target[batch_index, action_indices] = reward_batch + \
            self.gamma * torch.max(q_next, dim=1)[0]*terminal_batch
        self.epsilon = max(self.epsilon * self.eps_dec, self.eps_min)
        loss = self.net.loss(q_target, q).to(self.net.device)
        loss.backward()
        self.net.optimizer.step()
