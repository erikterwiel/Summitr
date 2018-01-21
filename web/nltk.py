import boto3
import bs4

dynamodb = boto3.resource('dynamodb')
s3 = boto3.resource('s3')

recents = dynamodb.Table('recents')
table = dynamodb.Table('reports')

time = int(input())

info = recents.get_item(
    Key={
        'time': time,
    }
)

response = table.get_item(
    Key={
        'title': txt,
    }
)

identifier = info['Item']['identifier']
post_type = info['Item']['type']
report_item = response['Item']['report']

client = boto3.client('comprehend',region_name='us-west-2')

def load_pictures(txt):
    for key in bucket.objects.all():
        print(key.key)
    #obj = s3.Object('mountainviews', 'key')
    #file_contents = obj.get()["Body"].read()
    #print file_contents.count('\n')

def sentimenter(id):
    with open("index.html") as inf:
        txt = inf.read()
        soup = bs4.BeautifulSoup(txt)

    new_tag = soup.new_tag('div class="posts"')
    soup.body.insert(4, new_tag)

    new_title = soup.new_tag('h2')
    new_title.insert(0,response['Item']['title'])
    new_tag.append(new_title)
    
    for key in bucket.objects.all():
        print(key.key)

    new_pic = soup.new_tag('img',src='githubprof.png')
    new_tag.append(new_pic)

    new_report = soup.new_tag('p')
    report_item = response['Item']['report']
    new_report.insert(0, report_item)
    new_tag.append(new_report)

    with open("index.html", "w") as outf:
        outf.write(str(soup))

    sent = client.detect_sentiment(
        Text=report_item,
        LanguageCode='en'
    )

    star_count = int(sent['SentimentScore']['Positive']*5)

    table.update_item(
        Key={
            'title': report_item,
        },
        UpdateExpression='SET ranking = :val1',
        ExpressionAttributeValues={
            ':val1': star_count
        }
    )


def display_photos(id):
    for key in bucket.objects.all():
        print(key.key)
    #obj = s3.Object('mountainviews', 'key')
    #file_contents = obj.get()["Body"].read()
    #print file_contents.count('\n')

def display_photos(id):
    with open("index.html") as inf:
        txt = inf.read()
        soup = bs4.BeautifulSoup(txt)

    new_tag = soup.new_tag('div class="posts"')
    soup.body.insert(4, new_tag)

    new_title = soup.new_tag('h2')
    new_title.insert(0,response['Item']['title'])
    new_tag.append(new_title)
    
    for key in bucket.objects.all():
        print(key.key)

    new_pic = soup.new_tag('img',src='githubprof.png')
    new_tag.append(new_pic)

    new_report = soup.new_tag('p')
    report_item = response['Item']['report']
    new_report.insert(0, report_item)
    new_tag.append(new_report)

    with open("index.html", "w") as outf:
        outf.write(str(soup))

if post_type == 'report':
    sentimenter(identifier)
elif post_type == 'photo':
    display_photos(identifier)
else:
    print('Invalid post type.')

