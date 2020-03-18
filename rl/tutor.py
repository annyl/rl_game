import torch.nn.modules
import torchvision
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


model = torchvision.models.resnet18(pretrained=True)
model.eval()
example = torch.rand(1, 3, 224, 224)
traced_script_module = torch.jit.trace(model, example)
traced_script_module.save("model.pt")

model = DeepQNetwork(lr=3e-3, input_dims=[8*8+1], fc1_dims=256, fc2_dims=256, n_actions=992)
model.save("df.pt")