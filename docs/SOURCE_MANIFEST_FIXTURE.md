# Source Manifest Fixture

This is the minimum shape Readora now expects from external repository manifests.
Repository sources are declarative: they store parser rules, not executable code.

```json
{
  "schemaVersion": 1,
  "repositoryId": "example-community",
  "name": "Example Community Sources",
  "maintainer": "Example Team",
  "description": "Sample Readora source repository.",
  "trustLevel": "community",
  "sources": [
    {
      "id": "example_json",
      "name": "Example JSON Source",
      "version": "1",
      "language": "en",
      "categories": ["Manga", "API"],
      "iconUrl": null,
      "parserType": "JSON_API",
      "baseUrl": "https://example.com",
      "minAppVersion": 1,
      "definition": {
        "popularEndpoint": "/api/popular?page={page}",
        "popularListPath": "$.items",
        "popularIdPath": "id",
        "popularTitlePath": "title",
        "popularCoverPath": "cover",
        "popularUrlPath": "url",
        "searchEndpoint": "/api/search?q={query}&page={page}",
        "searchListPath": "$.items",
        "searchIdPath": "id",
        "searchTitlePath": "title",
        "searchCoverPath": "cover",
        "searchUrlPath": "url",
        "detailEndpoint": "/api/title/{id}",
        "detailTitlePath": "title",
        "detailCoverPath": "cover",
        "detailAuthorPath": "author",
        "detailStatusPath": "status",
        "detailDescriptionPath": "description",
        "detailTagsPath": "tags",
        "chaptersEndpoint": "/api/title/{id}/chapters",
        "chaptersListPath": "$.chapters",
        "chaptersIdPath": "id",
        "chaptersNumberPath": "number",
        "chaptersTitlePath": "title",
        "chaptersUrlPath": "url",
        "chaptersDatePath": "date",
        "pagesEndpoint": "/api/chapter/{id}/pages",
        "pagesListPath": "$.pages",
        "pagesUrlPath": "imageUrl",
        "headers": {
          "User-Agent": "Readora/0.1"
        }
      }
    }
  ]
}
```

Validation rules currently enforced:
- `schemaVersion` must be supported by this app build.
- `trustLevel` must be `official`, `community`, or `untrusted`.
- Source `baseUrl` must be HTTPS.
- Source `parserType` must be `JSON_API`, `HTML_CSS`, or `RSS` aliases.
- JSON/HTML sources must include a parser `definition`.
