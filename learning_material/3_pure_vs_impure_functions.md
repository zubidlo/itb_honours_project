
**Recap**
> - In the last section, I made a suggestion that there is a way to have fun while creating a software. I claimed that functional programming gives us a better way to design a software and reason about it.
> - In the following sections, I will discuss functions, the building blocks of functional programs. We first must understand a what functions are, what characteristics they have, the rules they follow and how to work with them in the Scala programming language.

### Functions Everywhere 1 - Pure Functions

Well, functional programming is a programming style where programs are constructed using only **pure functions**. What are these pure functions you may ask?

Well, pure function is any function with following properties:

1. The function **always** evaluates to the same result given the same argument. The word always is emphasized here. The pure function can return the same result for different arguments, but it can never return different results for the same argument.

2. Evaluation of the result does not cause any semantically observable **side effect**. Well, what are these side effects then? In Computer Science, a function or expression is said to have a side effect if it modifies some state or has an interaction with calling functions or the outside world. Basically, a function has a side effect if it does something else than returning a result. Some examples of side effects:

    -   Modifying a variable or a data structure in place

    -   Throwing an exception or halting with an error

    -   Writing to or reading from the console or a file

    -   Drawing on the screen, updating a database, printing on a
        printer

**Example:** Common Pure and Impure Functions

```
sin(x)
length(list)
random(seed)
printf(string)
```

Functions `sin(x)` and `length(list)` are pure functions because they always return the same result for the same argument and do nothing else. Funciton `sin(0)` returns always 0 and `length` of the same list is always the same result.

Functions `random(seed)` and `printf(string)` are impure functions because `random(1)` will not always return the same result. It will return a random number from 0 to 1. Function `printf(“hello world”)` will print the string literal to the console and return no result. Even that is not guaranteed. Sometimes it can throw an exception if the console is not available for example. Impure functions are called **procedures** in computer science.

**Example:** Function definitions in Scala

```scala
        def add1(a: Int, b: Int): Int = return a + b;
        def add2(a: Int, b: Int): Int = a + b
        def add3(a: Int, b: Int) = a + b
        val add4: (Int,Int) => Int = (a: Int, b: Int) => a + b
        val add5 = (a: Int, b: Int) => a + b
        def add6(a: Int)(b: Int) = a + b
        val add7 = Int => Int => Int = a => b => a + b
```

1. On line 1 is a function definition where I defined a function `add1` which takes two arguments of type `Int` and returns `Int` result which is just the addition of the arguments.

2. `add2` defines exactly same function without `return` word and `semicolon`

3. `add3` is the same function with return type left out

4. Line 5 is not a function definition statement, but assignment to an immutable value named `add4`. The value of an anonymous function described by `(a: Int, b: Int) => a + b` was assigned to the name `add4`. This form of function description is called a **function literal** or a **lambda expression**. It’s the same function with emphasis on it’s type: `(Int, Int) => Int`. It’s a type of a function which takes 2 `Int` arguments and returns `Int` result.

5. `add5` is again a value which holds a function literal. This time the type is omitted and the function is only expressed by the lambda expression.

6. `add6` is again the same function defined in a **curried form**, which we will discuss later.

7. `add7` is again the same function value, but with it’s type `Int => Int => Int` emphasizes the curried form and the fact that **every function is a function which takes one argument and returns one result**. We will discuss these ideas later as well.

Here are the the function call statements where 4 and 5 were passed as arguments. All seven functions will return 9 as the result.

```scala
        add1(4,5); add2(4,5); add3(4,5); add4(4,5); add5(4,5)
        add6(4)(5); add7(4)(5);
```

Here are some important points to realize:

-   All seven functions represent the same function which adds two Int values and returns the result.

-   This function is a pure function because it always returns the same value 9 for the same arguments 4, 5 and doesn’t do any side effects.

-   In Scala and functional programming in general, functions are values. The same way as objects are values. We can assign functions to variables, pass them as arguments to other functions, create them inside the functions and return them as results of function computations. Later we will see that as long as the functions are pure they hold other very useful characteristics we can utilize in our functional programs.

-   The functions can be expressed as literals. The same way we can create anonymous objects or a string literal `“hello world”`, we can create a function literal in a form of a lambda expression.
