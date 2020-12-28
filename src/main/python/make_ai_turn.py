from ai import AI
from game import Game
import os

dir = os.path.dirname(os.path.abspath(__file__))

with open(os.path.join(dir, 'occupied_fields.txt'), 'r') as f:
    x_fields = list(map(int, f.readline().split()))
    o_fields = list(map(int, f.readline().split()))
    game = Game(22, 5)
    for f in x_fields:
        game.remaining.remove(f)
        game.board[game.position_dict[f][0]][game.position_dict[f][1]] = 'X'
        # print game.position_dict[f]
    for f in o_fields:
        game.remaining.remove(f)
        game.board[game.position_dict[f][0]][game.position_dict[f][1]] = 'O'
        # print game.position_dict[f]
    pos = AI('Computer', 'X' if len(x_fields) == len(o_fields) else 'O').turn(game)
    with open(os.path.join(dir, 'answer.txt'), 'w') as f:
        f.writelines([str(game.position_dict[pos][0]), ' ', str(game.position_dict[pos][1])])
