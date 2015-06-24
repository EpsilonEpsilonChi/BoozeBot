import firebase
from pprint import pprint

URL = 'burning-torch-4312'

def queue_handler(response):
    pprint(response)

fb_queue = firebase.subscriber(URL + "/queue", queue_handler)
fb_queue.start()
