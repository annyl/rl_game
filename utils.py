from torch import Tensor

def action_is_legal(action: Tensor, legal_actions: Tensor) -> bool:
    from_equals = legal_actions[:, 0] == action[0]
    to_equals = legal_actions[:,1] == action[1]
    equals = from_equals & to_equals
    nonzero = equals.nonzero()
    return nonzero.numel() > 0