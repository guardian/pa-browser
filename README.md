PA API Browser
==============

This project is a simple browser on the PA API so I can look at the
different parts of the API..

## Running it

Export your api key in the shell where you'll be running the application:

```bash
export PA_API_KEY='your-api-key'
```

Then run it like normal for a Play application:

    play run

Or if you don't have the play binary available:

    sbt run

To use a nonstandard port you can provide the port as an argument

    play -Dhttp.port=9001 run

