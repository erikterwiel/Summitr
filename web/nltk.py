import boto3
dynamodb = boto3.resource('dynamodb')

table = dynamodb.Table('test')
client = boto3.client('comprehend',region_name='us-west-2')

def sentimenter(txt):
    response = table.get_item(
        Key={
            'test1': txt,
        }
    )

    report_item = response['Item']['report']

    response = client.detect_sentiment(
        Text=report_item,
        LanguageCode='en'
    )

    star_count = int(response['SentimentScore']['Positive']*5)

    table.update_item(
        Key={
            'test1': txt,
        },
        UpdateExpression='SET rating = :val1',
        ExpressionAttributeValues={
            ':val1': star_count
        }
    )

to_analyze = str(input())
sentimenter(to_analyze)