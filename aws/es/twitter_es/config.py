
# Go to http://apps.twitter.com and create an app.
# The consumer key and secret will be generated for you after
consumer_key="y0ohz9Nse2mTJnxKm2NCzrPFV"
consumer_secret="2B6cxna8t7A2M52XZXmIcXZ0QUEqdUI1mnp66we9vXJo5ziCTe"

# After the step above, you will be redirected to your app's page.
# Create an access token under the the "Your access token" section
access_token="98782569-bjfBOHPsB2p68wU5GnDiGzPTNyCvFiDyhTcgyN1IF"
access_token_secret="jt5RP04Rn2SyePXBLcdYEmWeIK4avygYu64ury1B7QHeh"

#Application will stop streaming (per run) after max_tweets have been sent
max_tweets = 10000

#Elasticsearch host and port
es_host = 'search-ex-test-hrwiwbkbpn6wbbwqqsivrvplyi.us-east-1.es.amazonaws.com'
es_port = 80
es_bulk_chunk_size = 10 #number of documents to index in a single bulk operation
