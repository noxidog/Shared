# Design Reflections on an Information Distance Metric

This document outlines the rationale behind a compression-based heuristic for estimating the informational distance between two strings, inspired by the concept of Kolmogorov complexity.

## Core Idea

The central idea is to approximate a string’s entropy via the length of its compressed representation. Strings that compress well tend to contain internal structure or redundancy; strings that compress poorly are more complex. The design challenge lies in meaningfully comparing two strings while considering their intrinsic entropy.

## Rejection of Levenshtein Distance

I did not consider string distance metrics like the Levenshtein edit distance between two strings, because not all substitutions are the same. As a simple example, given two strings that differ only by N substitutions, their Levenshtein distance will be exactly the same regardless of whether the substituted characters were all the same or different. The first case will produce a much more informationally poor string, while the latter will have much greater entropy. I found this lack of resolution—provided by what is essentially a naive approach—to be incongruent with the underlying ideas of Kolmogorov complexity.

## Design Challenges

To compare two strings meaningfully, I first needed to construct a common basis that allows for consistent measurement. My initial approach involved concatenating the strings and compressing the result using a shared dictionary derived from both. However, this violated commutativity—changing the order of concatenation affected compression—and it also risked violating identity, where the distance between a string and itself should be zero.

## Symmetric Strategy

I then explored a symmetric strategy: using each string as a preset dictionary to compress the other, yielding two directional values, `C(A | B)` and `C(B | A)`. These approximate how well one string "explains" the other. While taking the maximum or the difference of these values seemed promising, neither preserved the identity property reliably, due to quirks in how real-world compressors handle preset dictionaries.

## Final Approach

Ultimately, I adopted a normalized and symmetric formulation that combines both directional measures. This approach preserves identity and symmetry while offering a practical approximation of Kolmogorov distance.

## Examples and Observations

**Example 1:**
0
"the quick brown fox"
"the quick brown fox"

*A string compared to itself returns 0 informational distance.*

---

**Example 2:**
51
"the quick brown fox"
"the quick brown fox jumps over the lazy doggie"

*A string compared to a string which contains it.*

---

**Example 3:**
49
"the quick brown fox"
"the rabbits eat carrots and almost never humans"

*Two strings have no common words. I wish the distance of this was larger than the one in the example above, but I think the length differences have introduced this effect.*

---

**Example 4:**
0
aaa
aaaaaaaa

*A string compared to a multiplied version of itself. I think of this as a feature at this point.*

---

**Example 5:**
1
aaa
abababab

*A string compared to a longer, slightly more complicated string. Maybe 1 is too small of a difference. Definitely worth investigating further.*

---

**Example 6:**
3
aba
abababab

*A more complicated string compared to an inexact multiplication of itself.*

---

**Example 7:**
3
aaab
abababab

*This example is similar to the one above. The fact that the distances are the same is a good thing to my mind.*

---

**Example 8:**
10
aaab
joi-opafs

*A string compared to a very different string of comparable length. This works for me intuitively.*

---

**Example 9:**
1
aaab
abcabcab

*Similar example as above, except the strings are more similar to each other. I think this is a good result.*

## Conclusion

As these examples illustrate, while further refinement may improve sensitivity to structural nuance, the current formulation performs well when compared to my intuitive expectations. I consider it a solid starting point.
