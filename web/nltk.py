import boto3

# Get the service resource.
dynamodb = boto3.resource('dynamodb')

# Instantiate a table resource object without actually
# creating a DynamoDB table. Note that the attributes of this table
# are lazy-loaded: a request is not made nor are the attribute
# values populated until the attributes
# on the table resource are accessed or its load() method is called.
table = dynamodb.Table('reports')

print(table.creation_date_time)

response = table.get_item(
    Key={
        'title': 'PennApps',
    }
)
item = response['Item']
print(item)