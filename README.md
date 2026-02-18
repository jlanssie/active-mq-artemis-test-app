# ActiveMQ Artemis Test App

A Java app that makes TCP requests to an ActiveMQ Artemis for testing purposes.

## Installation

Install with Maven using the provided settings.xml

```
mvn clean install -s settings.xml -T 1C
```

## Usage

Run the Producer class to produce a message to an ActiveMQ queue.
Or run the Subscriber class to receive messages from an ActiveMQ  queue.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss the proposed changes.

Please ensure you update tests as appropriate.

## License

[GNU GPL 3.0](https://www.gnu.org/licenses/gpl-3.0.en.html)
