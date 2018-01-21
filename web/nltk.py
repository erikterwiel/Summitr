import boto3

# Get the service resource.
dynamodb = boto3.resource('dynamodb')

table = dynamodb.Table('test')
client = boto3.client('comprehend',region_name='us-west-2')

def sentimenter(post_name):
    report = table.get_item(
        Key={
            'test1': 'Good morning',
        }
    )
    report_item = report['Item']['report']

    response = client.detect_sentiment(
        Text=report_item,
        LanguageCode='en'
    )
    star_count = int(response['SentimentScore']['Positive']*5)
    print(star_count)
    table.update_item(
    Key={
        'test1': 'Good morning',
        'rating': 0
        },
    UpdateExpression='SET rating = :val1',
    ExpressionAttributeValues={
        ':val1': star_count
        }
    )

to_analyze = str(input())
sentimenter(to_analyze)