import boto3
import bs4
import botocore

s3 = boto3.resource('s3')

bucket = s3.Bucket('mountainviews')
s3.meta.client.head_bucket(Bucket='mountainviews')

dynamodb = boto3.resource('dynamodb')

recents = dynamodb.Table('recents')
table = dynamodb.Table('reports')
pictures = dynamodb.Table('photos')

time = int(input())

info = recents.get_item(
    Key={
        'time': time,
    }
)

identifier = info['Item']['identifier']
post_type = info['Item']['type']
userid = info['Item']['username']

if post_type == 'report':
    response = table.get_item(
        Key={
            'title': identifier,
        }
    )
    report_item = response['Item']['report']

elif post_type == 'photo':
    response = recents.get_item(
        Key={
            'time': time,
        }
    )
    pics = pictures.get_item(
        Key={
            'filename': identifier,
        }
    )
    caption = pics['Item']['caption']

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
        soup = bs4.BeautifulSoup(txt,"html.parser")

    new_tag = soup.new_tag('div class="posts"')
    soup.body.insert(4, new_tag)

    new_title = soup.new_tag('h2')
    new_title.insert(0,response['Item']['title'])
    new_tag.append(new_title)

    misc = soup.new_tag('p')
    misc.insert(0, str(response['Item']['date'])+"|"+str(response['Item']['location'])+"|"+str(response['Item']['distance'])+"|"+str(userid))
    new_tag.append(misc)

    ######
    s3.Bucket("mountainviewer").download_file(str(id), 'my_local_image.jpg')
    new_pic = soup.new_tag('img',src='my_local_image.jpg')
    new_tag.append(new_pic)

    new_report = soup.new_tag('p')
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
            'title': id,
        },
        UpdateExpression='SET ranking = :val1',
        ExpressionAttributeValues={
            ':val1': star_count
        }
    )



def display_photos(id):
    with open("index.html") as inf:
        txt = inf.read()
        soup = bs4.BeautifulSoup(txt, "html.parser")

    new_tag = soup.new_tag('div class="posts"')
    soup.body.insert(4, new_tag)
    ##insert stars
    #for key in bucket.objects.all():
        #print(key.key)
    user_id = soup.new_tag('p')
    user_id.insert(0, userid)
    new_tag.append(user_id)

    s3.Bucket("mountainviewer").download_file(str(id), 'my_local_image.jpg')
    new_pic = soup.new_tag('img',src='my_local_image.jpg')
    new_tag.append(new_pic)

#####
    new_caption = soup.new_tag('p')
    new_caption.insert(0, caption)
    new_tag.append(new_caption)

    with open("index.html", "w") as outf:
        outf.write(str(soup))

    sent = client.detect_sentiment(
        Text=caption,
        LanguageCode='en'
    )

    star_count = int(sent['SentimentScore']['Positive']*5)

    pictures.update_item(
        Key={
            'filename': id,
        },
        UpdateExpression='SET ranking = :val1',
        ExpressionAttributeValues={
            ':val1': star_count
        }
    )

if post_type == 'report':
    sentimenter(identifier)
elif post_type == 'photo':
    display_photos(identifier)
else:
    print('Invalid post type.')

