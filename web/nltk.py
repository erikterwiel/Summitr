import boto3

client = boto3.client('comprehend',region_name='us-west-2')

response = client.detect_sentiment(
    Text='FUCK is so fucking retarded I do not understand why their documentation is less organized than an autistic brain. Fuck this bullshit!',
    LanguageCode='en'
)
print(int(response['SentimentScore']['Positive']*5)