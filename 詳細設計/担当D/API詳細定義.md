


```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "CategoryListResponse",
  "type": "object",
  "properties": {
    "categories": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "category_id": { "type": "integer" },
          "name":        { "type": "string" }
        },
        "required": ["category_id","name"]
      }
    }
  },
  "required": ["categories"]
}
```

