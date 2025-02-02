package com.example.thirty

//TODO. calculateScore har fortfarande potentiella list fel med removeAt delarna.

//This is a class which manages the calculations and score values for the app.
class Logic {
    val scoreMap = HashMap<String, Int>()

    //This is a brute force algorithm for the score system of the game. I don't really like it but it's all that I could come up with that works.
    //After looking this problem up it seems that it is a P != NP problem. So the solution was quite good maybe. But using this for more dices would take to long.
    fun calculateScore(diceValues: List<Int>, target: String): Int {
        var highestCombination = 0  // To store the highest combination of the sum
        val unUsedDices = diceValues.toMutableList()  // Create list to hold track of unused dices
        val targetSum = target.toIntOrNull() ?: 0  // Transform String option to integer. Default to 0 if conversion fails

        if (targetSum != 0) {
            // Check one dice at once
            for (a in unUsedDices.size - 1 downTo 0) {
                if (unUsedDices[a] == targetSum) {
                    highestCombination += 1
                    unUsedDices.removeAt(a)  // Remove the element at index 'a'
                }
            }

            // Check two dice at once
            // The while loops here is to control that the algorithm doesn't skip some dice at certain scenarios
            if (unUsedDices.size > 2) {
                mainLoop@while (true) {
                    for (a in unUsedDices.size - 1 downTo 0) {
                        for (b in a - 1 downTo 0) {
                            if (unUsedDices[a] + unUsedDices[b] == targetSum) {
                                highestCombination += 1
                                unUsedDices.removeAt(a)
                                unUsedDices.removeAt(b)
                                continue@mainLoop
                            }
                        }
                    }
                    break
                }
            }

            // Check three dice at once
            // The while loops here is to control that the algorithm doesn't skip some dice at certain scenarios
            if (unUsedDices.size > 3) {
                mainLoop@ while (true)  {
                    for (a in unUsedDices.size - 1 downTo 0) {
                        for (b in a - 1 downTo 0) {
                            for (c in b - 1 downTo 0) {
                                if (unUsedDices[a] + unUsedDices[b] + unUsedDices[c] == targetSum) {
                                    highestCombination += 1
                                    unUsedDices.removeAt(a)
                                    unUsedDices.removeAt(b)
                                    unUsedDices.removeAt(c)
                                    continue@mainLoop
                                }
                            }
                        }
                    }
                    break
                }
            }

            // Check for four dice at once
            if (unUsedDices.size > 4) {
                outer@for (a in unUsedDices.size - 1 downTo 0) {
                    for (b in a - 1 downTo 0) {
                        for (c in b - 1 downTo 0) {
                            for (d in c - 1 downTo 0) {
                                if (unUsedDices[a] + unUsedDices[b] + unUsedDices[c] + unUsedDices[d] == targetSum) {
                                    highestCombination += 1
                                    unUsedDices.removeAt(a)
                                    unUsedDices.removeAt(b)
                                    unUsedDices.removeAt(c)
                                    unUsedDices.removeAt(d)
                                    break@outer
                                }
                            }
                        }
                    }
                }
            }

            // Check five dice at once
            if (unUsedDices.size > 5) {
                outer@ for (a in unUsedDices.size - 1 downTo 0) {
                    for (b in a - 1 downTo 0) {
                        for (c in b - 1 downTo 0) {
                            for (d in c - 1 downTo 0) {
                                for (e in d - 1 downTo 0) {
                                    if (unUsedDices[a] + unUsedDices[b] + unUsedDices[c] + unUsedDices[d] + unUsedDices[e] == targetSum) {
                                        highestCombination += 1
                                        unUsedDices.removeAt(a)
                                        unUsedDices.removeAt(b)
                                        unUsedDices.removeAt(c)
                                        unUsedDices.removeAt(d)
                                        unUsedDices.removeAt(e)
                                        break@outer
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Check six dice at once
            var allTotal = 0
            if (unUsedDices.size > 6) {
                for (a in unUsedDices.size - 1 downTo 0) {
                    allTotal += unUsedDices[a]
                }
                if (allTotal == targetSum) {
                    highestCombination += 1
                }
            }
        }

        // Calculate low choice
        else {
            var lowTotal = 0
            for (a in unUsedDices.size - 1 downTo 0) {
                if (unUsedDices[a] <= 3) {
                    lowTotal += unUsedDices[a]
                }
            }
            scoreMap[target] = lowTotal
            return lowTotal
        }

        scoreMap[target] = highestCombination * targetSum
        return highestCombination * targetSum  // Return score from number choice of game. Highest combinations of the chosen target.
    }

    //This function returns the total score for the current round
    fun getTotalScore(): Int {
        return scoreMap.values.sum()
    }

    //This function clears the score hashmap for the class so that it can be reused the next game.
    fun gameIsOver() {
        scoreMap.clear()
    }
}
