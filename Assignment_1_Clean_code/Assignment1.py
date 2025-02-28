import random

def roll_die(number_of_sides):
    return random.randint(1, number_of_sides)

def main():
    sides_of_die = 6
    continue_rolling = True
    while continue_rolling:
        user_input = input("Ready to roll? Enter 'Q' to quit: ")
        if user_input.lower() != 'q':
            roll_result = roll_die(sides_of_die)
            print(f"You rolled a {roll_result}.")
        else:
            continue_rolling = False
