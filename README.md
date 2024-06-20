[![CI Build](https://github.com/elwaxoro/advent-of-code/actions/workflows/gradle.yml/badge.svg)](https://github.com/elwaxoro/advent-of-code/actions/workflows/gradle.yml)

# advent-of-code
Just having some fun with https://adventofcode.com/ challenges, solved with Kotlin

## How to has?

Solve puzzles as unit tests, create each day in a file like `advent/src/test/kotlin/org/elwaxoro/advent/y2022/Dec01.kt`

Extend `PuzzleDayTester`, override part1 and part2 functions as you write them:

```
class Dec01 : PuzzleDayTester(1, 2020) {
    override fun part1(): Any = "Yay"
    override fun part2(): Any = "Boo"
}
```

By default, these tests fail till they're implemented. Any return counts as passing.
Since the test doesn't know the right answer, check the output for what to copy into the advent of code test boxes

## Input files
Input files should go in test/resources based on year and date like `advent/src/test/resources/2022/Dec01.txt`

Note: input files are totally optional! Only use them if it makes sense for the puzzle you're solving

PuzzleDayTester has a `load` function to help find them and parse them in a simple way (one string per newline by default)

```
load(testNum: Int? = null, delimiter: String = "\n"): List<String>
```

Optional: if `testNum` is set on the load function, a file with the format `Dec01-test-1.txt` is looked for instead of `Dec01.txt`
This lets you separate the real input file from the samples (which can vary part 1 vs part 2) 

## Extensions / Helpers
`main/kotlin` has a collection of extensions and helper classes to solve certain kinds of puzzles. Ignore these if you want. Or use them. I won't tell you what to do.

## Disclaimers
Running the code for previous days / years isn't guaranteed to work or give correct output. Same goes for some of the helper functions. Most things *should* work, but ymmv! I tend to solve these quickly, then come back later to refactor. Sometimes the refactor breaks it and then I run out of time to fight with it, so it stays broken. Good luck!
