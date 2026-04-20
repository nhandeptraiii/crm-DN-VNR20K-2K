import json
import os

# 1. Postman_1_Auth_Users_Roles.json
file1 = "Postman_1_Auth_Users_Roles.json"
if os.path.exists(file1):
    with open(file1, "r", encoding="utf-8") as f:
        data1 = json.load(f)
    
    # 2.2 Create User
    for item in data1["item"][1]["item"]:
        if item["name"] == "2.2. Create User":
            body = json.loads(item["request"]["body"]["raw"])
            body["region"] = "CTO"
            body["communeIds"] = [1]
            item["request"]["body"]["raw"] = json.dumps(body, indent=2, ensure_ascii=False)
            break
            
    # 2.4 Update User
    for item in data1["item"][1]["item"]:
        if item["name"] == "2.4. Update User":
            body = json.loads(item["request"]["body"]["raw"])
            body["region"] = "CTO"
            body["communeIds"] = [1]
            item["request"]["body"]["raw"] = json.dumps(body, indent=2, ensure_ascii=False)
            break

    with open(file1, "w", encoding="utf-8") as f:
        json.dump(data1, f, indent=2, ensure_ascii=False)


# 2. Postman_2_Enterprises.json
file2 = "Postman_2_Enterprises.json"
if os.path.exists(file2):
    with open(file2, "r", encoding="utf-8") as f:
        data2 = json.load(f)
        
    for item in data2["item"]:
        if item["name"] == "2.1. Create Enterprise":
            body = json.loads(item["request"]["body"]["raw"])
            body["type"] = "SME"
            body["communeId"] = 1
            item["request"]["body"]["raw"] = json.dumps(body, indent=2, ensure_ascii=False)
            
        elif item["name"] == "2.3. Update Enterprise":
            body = json.loads(item["request"]["body"]["raw"])
            body["type"] = "SME"
            body["communeId"] = 1
            item["request"]["body"]["raw"] = json.dumps(body, indent=2, ensure_ascii=False)
            
        elif item["name"] == "2.2. Search Enterprises":
            # Add parameters type and region
            url = item["request"]["url"]
            if "query" not in url:
                url["query"] = []
            
            has_type = False
            for q in url["query"]:
                if q["key"] == "typeStr": has_type = True
            
            if not has_type:
                url["query"].append({"key": "typeStr", "value": "SME"})
                url["query"].append({"key": "regionStr", "value": "CTO"})
                url["raw"] += "&typeStr=SME&regionStr=CTO"
                
    with open(file2, "w", encoding="utf-8") as f:
        json.dump(data2, f, indent=2, ensure_ascii=False)


# 3. Postman_2.1_Enterprises_Excel.json
file21 = "Postman_2.1_Enterprises_Excel.json"
if os.path.exists(file21):
    with open(file21, "r", encoding="utf-8") as f:
        data21 = json.load(f)
        
    for item in data21["item"]:
        url = item["request"]["url"]
        if "query" not in url:
            url["query"] = []
            
        has_group = False
        for q in url["query"]:
            if q["key"] == "group": has_group = True
            
        if not has_group:
            url["query"].append({"key": "group", "value": "SME"})
            if "?" in url["raw"]:
                url["raw"] += "&group=SME"
            else:
                url["raw"] += "?group=SME"

    with open(file21, "w", encoding="utf-8") as f:
        json.dump(data21, f, indent=2, ensure_ascii=False)


# 4. Create Postman_9_Locations.json
loc_data = {
  "info": {
    "name": "9. CRM Locations (Tỉnh Cụm Xã)",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "9.1 Lấy danh sách Cụm theo Tỉnh",
      "request": {
        "method": "GET",
        "header": [
          { "key": "Authorization", "value": "Bearer {{access_token}}" }
        ],
        "url": {
          "raw": "http://localhost:8080/locations/clusters?region=CTO",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["locations", "clusters"],
          "query": [
            { "key": "region", "value": "CTO" }
          ]
        }
      }
    },
    {
      "name": "9.2 Lấy danh sách Xã theo Cụm",
      "request": {
        "method": "GET",
        "header": [
          { "key": "Authorization", "value": "Bearer {{access_token}}" }
        ],
        "url": {
          "raw": "http://localhost:8080/locations/communes?clusterId=1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["locations", "communes"],
          "query": [
            { "key": "clusterId", "value": "1" }
          ]
        }
      }
    }
  ]
}

with open("Postman_9_Locations.json", "w", encoding="utf-8") as f:
    json.dump(loc_data, f, indent=2, ensure_ascii=False)

print("SUCCESS")
