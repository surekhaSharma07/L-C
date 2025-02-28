import random

def is_valid_input(user_input):
    return user_input.isdigit() and 1 <= int(user_input) <= 100

def main():
    current_number = random.randint(1, 100)
    has_guessed_correctly = False
    guess = input("Guess a number between 1 and 100: ")
    number_of_attempts = 0

    while not has_guessed_correctly:
        if not is_valid_input(guess):
            guess = input("Invalid input. Please enter a number between 1 and 100: ")
            continue
        else:
            number_of_attempts += 1
            guess = int(guess)

        if guess < current_number:
            guess = input("Too low. Guess again: ")
        elif guess > current_number:
            guess = input("Too high. Guess again: ")
        else:
            print(f"Congratulations! You guessed the number in {number_of_attempts} attempts.")
            has_guessed_correctly = True

    main()


