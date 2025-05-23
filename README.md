```markdown
# Design Reflections on an Information Distance Metric

This document outlines the rationale behind a compression-based heuristic for estimating the informational distance between two strings, inspired by the concept of **Kolmogorov complexity**.

The central idea is to approximate a string’s entropy via the length of its compressed representation. Strings that compress well tend to contain internal structure or redundancy; strings that compress poorly are more complex. The design challenge lies in meaningfully comparing two strings while considering their intrinsic entropy.

I did not consider string distance metrics like the **Levenshtein edit distance** between two strings, because not all substitutions are the same. 

> As a simple example, given two strings that differ only by N substitutions, their Levenshtein distance will be exactly the same regardless of whether the substituted characters were all the same or different.  
> The first case will produce a much more informationally poor string, while the latter will have much greater entropy.  
> I found this lack of resolution—provided by what is essentially a naive approach—to be incongruent with the underlying ideas of Kolmogorov complexity.

To compare two strings meaningfully, I first needed to construct a common basis that allows for consistent measurement. 

My initial approach involved concatenating the strings and compressing the result using a shared dictionary derived from both. However, this violated **commutativity**—changing the order of concatenation affected compression—and it also risked violating **identity**, where the distance between a string and itself should be zero.

I then explored a **symmetric strategy**: using each string as a preset dictionary to compress the other, yielding two directional values, `C(A | B)` and `C(B | A)`. These approximate how well one string "explains" the other. 

While taking the maximum or the difference of these values seemed promising, neither preserved the identity property reliably, due to quirks in how real-world compressors handle preset dictionaries.

Ultimately, I adopted a **normalized and symmetric formulation** that combines both directional measures. This approach preserves identity and symmetry while offering a practical approximation of Kolmogorov distance.

---

## Examples

Below are a few illustrative examples of the code's results:

| Distance | String A                            | String B                                         | Notes                                                                 |
|----------|-------------------------------------|--------------------------------------------------|-----------------------------------------------------------------------|
| 0        | the quick brown fox                 | the quick brown fox                              | A string compared to itself returns 0 informational distance.         |
| 51       | the quick brown fox                 | the quick brown fox jumps over the lazy doggie   | A string compared to a string which contains it.                      |
| 49       | the quick brown fox                 | the rabbits eat carrots and almost never humans  | No common words. Would expect higher distance; length affects result. |
| 0        | aaa                                 | aaaaaaaa                                         | A string compared to a repeated version of itself. Seen as a feature. |
| 1        | aaa                                 | abababab                                         | Slightly more complicated comparison. Result maybe too small.         |
| 3        | aba                                 | abababab                                         | More complex string with inexact repetition.                          |
| 3        | aaab                                | abababab                                         | Similar to above; matching distance is a good sign.                   |
| 10       | aaab                                | joi-opafs                                        | Very different strings of similar length. Feels intuitive.            |
| 1        | aaab                                | abcabcab                                         | More similar strings; lower distance is sensible.                     |

---

As these examples illustrate, while further refinement may improve sensitivity to structural nuance, the current formulation performs well when compared to my intuitive expectations. I consider it a solid starting point.
```

Let me know if you'd like this turned into a downloadable `.md` file or rendered as HTML.
