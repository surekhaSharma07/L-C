def is_armstrong_number(number):
    # Initialize sum and count of digits
    armstrong_sum = 0
    digit_count = 0

    
    current_number = number
    while current_number > 0:
        digit_count += 1
        current_number //= 10

   
    current_number = number
    while current_number > 0:
        digit = current_number % 10
        armstrong_sum += digit ** digit_count
        current_number //= 10

    return armstrong_sum


input_number = int(input("\nPlease enter the number to check for Armstrong: "))


if input_number == is_armstrong_number(input_number):
    print(f"\n{input_number} is an Armstrong number.\n")
else:
    print(f"\n{input_number} is not an Armstrong number.\n")

