package com.line.ide.ui

data class Lesson(val title: String, val body: String, val code: String, val question: String, val answer: String)

val LESSONS = listOf(
    Lesson("1. Print and variables", "print() shows output. A variable stores a value, like x = 5.",
        "name = \"Line\"\nprint(\"Hello\", name)\n", "Which function shows output on the screen?", "print"),
    Lesson("2. Input and numbers", "input() reads text. Wrap it in int() or float() to get a number.",
        "a = int(input(\"A: \"))\nb = int(input(\"B: \"))\nprint(a + b)\n", "Which function converts text to a whole number?", "int"),
    Lesson("3. Operators", "Arithmetic: + - * / // % **\nComparison: == != < > <= >=\nLogic: and, or, not",
        "print(7 // 2, 7 % 2, 2 ** 3)\nprint(7 > 3 and 2 == 2)\n", "What is the value of 7 // 2?", "3"),
    Lesson("4. if / elif / else", "Conditions choose which block runs. Indentation matters.",
        "n = int(input(\"Number: \"))\nif n % 2 == 0:\n    print(\"even\")\nelse:\n    print(\"odd\")\n", "Which operator gives the remainder?", "%"),
    Lesson("5. Loops", "for repeats a fixed number of times. while repeats until its condition is false.",
        "for i in range(1, 6):\n    print(i, i * i)\n", "How many times does range(5) repeat?", "5"),
    Lesson("6. Strings", "Strings are sequences of characters. Index from 0 and slice with s[a:b].",
        "s = \"Python\"\nprint(s[0], s[-1], s[1:4], len(s))\nprint(s.upper())\n", "What is the index of the first character?", "0"),
    Lesson("7. Lists", "Lists hold many values. Use append, sort, len, max and loops.",
        "nums = [5, 2, 9]\nnums.append(1)\nnums.sort()\nprint(nums, max(nums), sum(nums))\n", "Which method adds an item to the end of a list?", "append"),
    Lesson("8. Functions", "A function packages code you can reuse, using def and return.",
        "def square(x):\n    return x * x\n\nprint(square(6))\n", "Which keyword defines a function?", "def")
)
