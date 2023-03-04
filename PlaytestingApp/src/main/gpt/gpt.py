import sys
import openai

request = sys.argv[1]
# request = "I want all users where the name is either tUser1 or tUser2 and also where the difficulty was rated as greater than or equal to 5."

with open ("./key.txt", "r") as f:
    openai.api_key = f.read()

prompt = ""
with open("./src/main/gpt/prompt.txt", "r") as f:
    prompt = f.read()
    prompt = prompt.replace("<<REQUEST>>", request)

response = openai.Completion.create(
    model="text-davinci-003",
    prompt=prompt,
    temperature=0,
    max_tokens=200,
    top_p=1
)

print(response["choices"][0]["text"])