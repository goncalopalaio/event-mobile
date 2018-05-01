# event-mobile

Latest apk is available here:

https://github.com/goncalopalaio/event-mobile/blob/master/apks/event-mobile.apk?raw=true

This is a debug APK. Install from external sources must be supported.

Server at: https://github.com/goncalopalaio/event-server/

## Supported features
- List current events
- Filter current events based in their category
- Create a new event with the default category

## Unsupported features

- Create a new event with a custom category
- Extensive filtering options

## A few notes

### Current state of development

This is extremely incomplete. 
Here's a list of what should be improved beyond the obvious:

- Better UI feedback. While saving an event there's no indication of failure or sucess at this time.
- Possibility to view current events. (already supported by the server) 
- Possibility to update events. (already supported by the server)
- Full data support. Not all data sent from the server is shown.
- Better caching. Currently everything is cached temporarily, objects should be stored in a database so they can be accessed offline.
- Handle permissions correctly. If the user revokes the permission there's no user friendly way of requesting the permissions again.
- Improve UX.
- Generate a release apk. Currently this is unsupported until a google maps api key is added to the release version.
- Improve handling of background tasks. There's some dropped frames in a few places.
