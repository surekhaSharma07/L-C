import random

def validate_guess(input_guess):
    return input_guess.isdigit() and 1 <= int(input_guess) <= 100

def number_guessing():
    random_value = random.randint(1, 100)
    guessed_correctly = False
    attempt_count = 0

    print("Welcome to the Number Guessing Challenge!")
    print("Please guess a number between 1 and 100.")

    while not guessed_correctly:
        user_guess = input("Enter your guess: ")

        if not validate_guess(user_guess):
            print("Invalid input. Enter a number between 1 and 100.")
            continue

        attempt_count += 1
        numeric_guess = int(user_guess)

        if numeric_guess < random_value:
            print("Too low. Try again.")
        elif numeric_guess > random_value:
            print("Too high. Try again.")
        else:
            print(f"Congratulations! You guessed the number in {attempt_count} attempts.")
            guessed_correctly = True

number_guessing()