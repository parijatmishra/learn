#!/usr/bin/python
# -*- coding: utf-8 -*-
'''
Created on Oct 20, 2015

@author: mentzera
'''
import re
import json
    
id_field = 'id_str'

tweet_mapping = {'properties': 
                    {'timestamp_ms': {
                                  'type': 'date'
                                  },
                     'text': {
                                  'type': 'string'
                              },
                     'coordinates': {
                          'properties': {
                             'coordinates': {
                                'type': 'geo_point'
                             },
                             'type': {
                                'type': 'string',
                                'index' : 'not_analyzed'
                            }
                          }
                     },
                     'user': {
                          'properties': {
                             'id': {
                                'type': 'long'
                             },
                             'name': {
                                'type': 'string'
                            }
                          }
                     }
                    }
                 }

    
def get_tweet(doc):
    tweet = {}
    #print ("doc : " + json.dumps(doc))
    #Some rudimentary error handling
    if ('id_str' not in doc or 
        'entities' not in doc or 
        'coordinates' not in doc or 
        'timestamp_ms' not in doc or 
        'user' not in doc or 
        'text' not in doc):
        return None
    tweet[id_field] = doc[id_field]
    tweet['hashtags'] = map(lambda x: x['text'],doc['entities']['hashtags'])
    tweet['coordinates'] = doc['coordinates']
    tweet['timestamp_ms'] = doc['timestamp_ms'] 
    tweet['text'] = doc['text']
    tweet['user'] = {'id': doc['user']['id'], 'name': doc['user']['name']}
    tweet['mentions'] = re.findall(r'@\w*', doc['text'])
    return tweet