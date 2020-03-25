import numpy as np
import torch
from torch import nn
from torch import optim
import torch.nn.functional as F


class DeepQNetwork(nn.Module):
    def __init__(self, lr, input_dims, fc_dims, n_actions):
        super(DeepQNetwork, self).__init__()
        self.fc1 = nn.Linear(*input_dims, fc_dims)
        self.fc2 = nn.Linear(fc_dims, fc_dims)
        self.fc3 = nn.Linear(fc_dims, fc_dims)
        self.fc4 = nn.Linear(fc_dims, fc_dims)
        self.fc5 = nn.Linear(fc_dims, fc_dims)
        self.fc6 = nn.Linear(fc_dims, fc_dims)
        self.fc7 = nn.Linear(fc_dims, fc_dims)
        self.fc8 = nn.Linear(fc_dims, n_actions)
        self.optimizer = optim.Adam(self.parameters(), lr=lr)
        self.loss = nn.MSELoss()
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.to(self.device)

    def forward(self, observation: torch.Tensor):
        state = observation.to(self.device)
        x = F.relu(self.fc1(state))
        x = F.relu(self.fc2(x))
        x = F.relu(self.fc3(x))
        x = F.relu(self.fc4(x))
        x = F.relu(self.fc5(x))
        x = F.relu(self.fc6(x))
        x = F.relu(self.fc7(x))
        actions = self.fc8(x)
        return actions


class Agent:
    def __init__(self, gamma, epsilon, lr, input_dims,
                 batch_size, action_space, max_mem_size=1000000,
                 eps_min=0.01, eps_dec=0.998466):
        self.n_actions = len(action_space)
        self.epsilon = epsilon
        self.batch_size = batch_size
        self.gamma = gamma
        self.action_space = action_space
        self.net = DeepQNetwork(lr, input_dims, 256, self.n_actions)
        self.mem_size = max_mem_size
        self.mem_cntr = 0
        self.state_memory = torch.zeros(max_mem_size, *input_dims)
        self.new_state_memory = torch.zeros(max_mem_size, *input_dims)
        self.action_memory = torch.zeros(max_mem_size, self.n_actions)
        self.reward_memory = torch.zeros(max_mem_size)
        self.terminal_memory = torch.zeros(max_mem_size)
        self.eps_dec = eps_dec
        self.eps_min = eps_min

    def store_transition(self, state, action, reward, new_state, terminal):
        index = self.mem_cntr % self.mem_size
        self.state_memory[index] = state
        actions = torch.zeros(self.n_actions)
        action_idx = ((self.action_space[:, 0] == action[0]) & (self.action_space[:, 1] == action[1])).nonzero().view(-1)
        actions[action_idx.item()] = 1.0
        self.action_memory[index] = actions
        self.reward_memory[index] = reward
        self.terminal_memory[index] = 1 - terminal
        self.new_state_memory[index] = new_state
        self.mem_cntr += 1

    def choose_action(self, observation: torch.Tensor):
        rand = np.random.random()
        if rand < self.epsilon:
            return self.action_space[np.random.randint(len(self.action_space))]
        actions = self.net.forward(observation)
        action = torch.argmax(actions).item()
        return self.action_space[action]

    def learn(self, decay:bool = True):
        if self.mem_cntr > self.batch_size:
            self.net.optimizer.zero_grad()
        max_mem = min(self.mem_cntr, self.mem_size)
        batch = np.random.choice(max_mem, self.batch_size)
        state_batch = self.state_memory[batch]
        action_batch = self.action_memory[batch]
        action_indices = action_batch.nonzero()[:,1]
        reward_batch = self.reward_memory[batch].to(self.net.device)
        terminal_batch = self.terminal_memory[batch].to(self.net.device)
        new_state_batch = self.new_state_memory[batch]

        q = self.net.forward(state_batch).to(self.net.device)
        q_target = self.net.forward(state_batch).to(self.net.device)
        q_next = self.net.forward(new_state_batch).to(self.net.device)
        batch_index = np.arange(self.batch_size, dtype=np.int32)
        q_target[batch_index, action_indices] = reward_batch + \
            self.gamma * torch.max(q_next, dim=1)[0]*terminal_batch
        if decay:
            self.epsilon = max(self.epsilon * self.eps_dec, self.eps_min)
        loss = self.net.loss(q_target, q).to(self.net.device)
        loss.backward()
        self.net.optimizer.step()

class MobileAgent(nn.Module):
    def __init__(self, agent:Agent):
        super(MobileAgent, self).__init__()
        self.action_space = agent.action_space
        self.net = torch.jit.trace(agent.net.cpu(), torch.rand(65).cpu())

    @torch.jit.export
    def choose_action(self, observation, legal_actions):
        actions = self.net.forward(observation)
        legal_actions_idx = torch.cat(
            [((self.action_space[:, 0] == legal_action[0]) &
              (self.action_space[:, 1] == legal_action[1])).nonzero()
             for legal_action in legal_actions])
        action = torch.argmax(actions[legal_actions_idx]).item()
        return self.action_space[legal_actions_idx[int(action)]][0]

    def forward(self, x):
        return self.net.forward(x)