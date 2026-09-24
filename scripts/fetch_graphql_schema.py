import json
import sys
import urllib.request

INTROSPECTION = """
query IntrospectionQuery {
  __schema {
    queryType { name } mutationType { name } subscriptionType { name }
    types { ...FullType }
    directives { name description locations args { ...InputValue } }
  }
}
fragment FullType on __Type {
  kind name description
  fields(includeDeprecated: true) { name description args { ...InputValue } type { ...TypeRef } isDeprecated deprecationReason }
  inputFields { ...InputValue }
  interfaces { ...TypeRef }
  enumValues(includeDeprecated: true) { name description isDeprecated deprecationReason }
  possibleTypes { ...TypeRef }
}
fragment InputValue on __InputValue { name description type { ...TypeRef } defaultValue }
fragment TypeRef on __Type { kind name ofType { kind name ofType { kind name ofType { kind name ofType { kind name ofType { kind name ofType { kind name ofType { kind name } } } } } } } }
"""


def named(type_ref):
    while type_ref:
        if type_ref.get("name"):
            return type_ref["name"]
        type_ref = type_ref.get("ofType")


def reachable_from(root, by_name):
    seen, stack = set(), [root]
    while stack:
        name = stack.pop()
        if name in seen or name not in by_name:
            continue
        seen.add(name)
        t = by_name[name]
        for field in (t.get("fields") or []) + (t.get("inputFields") or []):
            stack.append(named(field["type"]))
            stack.extend(named(arg["type"]) for arg in field.get("args") or [])
        stack.extend(named(ref) for ref in (t.get("interfaces") or []) + (t.get("possibleTypes") or []))
    return seen


def main(url, output):
    request = urllib.request.Request(url, data=json.dumps({"query": INTROSPECTION}).encode(),
                                     headers={"Content-Type": "application/json"})
    response = json.load(urllib.request.urlopen(request))
    if response.get("errors"):
        sys.exit(response["errors"])
    schema = response["data"]["__schema"]
    by_name = {t["name"]: t for t in schema["types"]}
    keep = reachable_from(schema["queryType"]["name"], by_name)
    schema["types"] = [t for t in schema["types"] if t["name"] in keep or t["name"].startswith("__")
                       or t["kind"] == "SCALAR"]
    schema["mutationType"] = None
    schema["subscriptionType"] = None
    with open(output, "w") as f:
        json.dump({"data": {"__schema": schema}}, f, indent=2)
    print(f"{len(schema['types'])} types written to {output}")


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
