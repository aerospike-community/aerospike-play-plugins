# Aerospike Transcoder Interface for serializing and deserializing POJOS

Java has a mechanism such that an object can be represented as a sequence of bytes. This sequence will
contain information about the object's type and all the types of data stored inside the object as well 
as the data itself. Serializing an object is a process of converting that object into a sequence of 
bytes. Deserialization is a process creating object back from the sequence of bytes.

### Installation:

In your gradle project, add following to your dependencies:

```
dependencies {
	compile project(':aerospike-transcoder')
}
```

This transcoder interface has two operations:
* encode(): This operation will take an object and return a byte array representation.

* decode(): This operation will take byte array and create an object from the sequence 
of bytes.

[FasterXML-jackson dataind](https://github.com/FasterXML/jackson-databind/wiki/Serialization-Features)  and [Fast Serialization](https://github.com/RuedigerMoeller/fast-serialization) are the two implementations provided by default.
