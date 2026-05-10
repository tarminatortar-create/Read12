# Safe Parser Strategy

## The Problem
Many reading apps download untrusted code (JavaScript, Python, or compiled extensions) from the internet and execute it dynamically. This poses a massive security risk to the user, as the executed code has full access to the app's networking and storage contexts.

## Our Approach: Declarative Parsers
Readora takes a fundamentally different approach. We do **not** allow dynamic code execution from external sources. Instead, we use **declarative parsing definitions**.

A source manifest provides a JSON object containing **rules** (like CSS selectors or JSONPath strings), not code. The Readora engine reads these rules and executes the parsing natively within the app.

### Benefits
1. **100% Safe:** Malicious actors cannot inject arbitrary code.
2. **Performant:** Parsing happens in compiled Kotlin, not via a heavy JavaScript runtime.
3. **Cross-Platform:** The declarative JSON format can easily be shared and interpreted by other apps.

## Supported Parser Types

1. **`JSON_API`**
   - Ideal for sources that have proper REST/GraphQL endpoints (e.g. MangaDex).
   - Rules are written using JsonPath-style dot-notation.

2. **`HTML_CSS`**
   - Ideal for standard web scrapers.
   - Rules are written using CSS selectors.

3. **`RSS`**
   - Standard feed parsing.

The `ParserEngine` is responsible for applying these definitions against the incoming HTTP string and mapping it to Readora's internal data models (`OnlineComic`, `OnlineChapter`).
