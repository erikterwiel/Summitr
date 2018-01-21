import boto3
import bs4

dynamodb = boto3.resource('dynamodb')

table = dynamodb.Table('reports')
client = boto3.client('comprehend',region_name='us-west-2')

response = table.get_item(
    Key={
        'title': txt,
    }
)

with open("index.html") as inf:
    txt = inf.read()
    soup = bs4.BeautifulSoup(txt)

new_tag = soup.new_tag('div class="posts"')
soup.body.insert(4, new_tag)
new_pic = soup.new_tag('img',src='githubprof.png')
new_tag.append(new_pic)
new_report = soup.new_tag('p')
new_report.insert(0, "new stuff")
new_tag.append(new_report)

with open("index.html", "w") as outf:
    outf.write(str(soup))

def sentimenter(txt):

    report_item = response['Item']['report']

    response = client.detect_sentiment(
        Text=report_item,
        LanguageCode='en'
    )

    star_count = int(response['SentimentScore']['Positive']*5)

    table.update_item(
        Key={
            'title': txt,
        },
        UpdateExpression='SET rating = :val1',
        ExpressionAttributeValues={
            ':val1': star_count
        }
    )

to_analyze = str(input())
sentimenter(to_analyze)