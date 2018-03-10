from __future__ import absolute_import, print_function
from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk
import config
from elasticsearch.exceptions import ElasticsearchException
from tweet_utils import get_tweet, id_field, tweet_mapping
from twython import TwythonStreamer
from requests.exceptions import ChunkedEncodingError

import json
import config

index_name = 'twitter'
doc_type = 'tweet'

mapping = { doc_type: tweet_mapping }
bulk_chunk_size = config.es_bulk_chunk_size

def create_index(es,index_name,mapping):
    print('ElasticSearch creating index {}...'.format(index_name))
    es.indices.create(index_name, body = {'mappings': mapping})

def load(tweets):
    es = Elasticsearch(host = config.es_host, port = config.es_port)

    if es.indices.exists(index_name):
        #print ('index {} already exists'.format(index_name))
        try:
            es.indices.put_mapping(doc_type, tweet_mapping, index_name)
        except ElasticsearchException as e:
            print('error putting mapping:\n'+str(e))
            print('deleting index {}...'.format(index_name))
            es.indices.delete(index_name)
            create_index(es, index_name, mapping)
    else:
        print('ElasticSearch index {} does not exist'.format(index_name))
        create_index(es, index_name, mapping)
    
    counter = 0
    bulk_data = []
    list_size = len(tweets)
    for doc in tweets:
        tweet = get_tweet(doc)
        if tweet == None:
            continue
        bulk_doc = {
            "_index": index_name,
            "_type": doc_type,
            "_id": tweet[id_field],
            "_source": tweet
        }
        #print (bulk_doc)
        bulk_data.append(bulk_doc)
        counter+=1
        
        if counter % bulk_chunk_size == 0 or counter == list_size:
            print ("ElasticSearch bulk index (index: {INDEX}, type: {TYPE})...".format(INDEX=index_name, TYPE=doc_type))
            success, _ = bulk(es, bulk_data)
            print ('ElasticSearch indexed %d documents' % success)
            bulk_data = []


class MyStreamer(TwythonStreamer):

    tweet_batch = []
    tweet_batch_count = 0 

    def on_success(self, data):

        if self.tweet_batch_count>0 and self.tweet_batch_count % config.es_bulk_chunk_size == 0:
            load(self.tweet_batch)
            self.tweet_batch = []
        else:
            self.tweet_batch.append(data)

        if config.max_tweets > 0 and self.tweet_batch_count > config.max_tweets:
            self.disconnect()

        self.tweet_batch_count += 1

    def on_error(self, status_code, data):
        print(status_code, data)


def twitter_read():
    while True:
        try:
            # Requires Authentication as of Twitter API v1.1
            stream = MyStreamer(config.consumer_key, config.consumer_secret, 
                config.access_token, config.access_token_secret)
            stream.statuses.sample()
        except ChunkedEncodingError:
            continue

twitter_read()
