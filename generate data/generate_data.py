import datetime
import json

# List of places
places = ["טבריה ישוב", "היכל פיס ארנה י-ם", "מרכז דתי חיפה", "חריש אולם גפן"]

# Initialize an empty list to store the JSON entries
json_entries = []

# Get the current date
today = datetime.date.today()

# Set the start and end time
#start_time = datetime.datetime(today.year, today.month, today.day, 9, 0)
#end_time = datetime.datetime(today.year, today.month, today.day, 14, 0)

# Create entries with 20-minute intervals
for i in range(2):
    start_time = datetime.datetime(today.year, today.month, today.day + i, 9, 0)
    end_time = datetime.datetime(today.year, today.month, today.day + i, 14, 0)

    current_time = start_time
    while current_time <= end_time:
        for place in places:
            entry = {
                "date": current_time.strftime("%Y-%m-%d"),
                "time": current_time.strftime("%H:%M"),
                "location": place,
                "occupied": ""
            }
            json_entries.append(entry)
        current_time += datetime.timedelta(minutes=20)

# Print the JSON entries
json_object = json.dumps(json_entries, indent=4)
 
# Writing to sample.json
with open("sample.json", "w") as outfile:
    outfile.write(json_object)

# You can also save the JSON entries to a file if needed.
