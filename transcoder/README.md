# Aerospike Transcoder Interface for serializing and deserializing POJOS

Java has a mechanism such that an object can be represented as a sequence of bytes. This sequence will
contain information about the object's type and all the types of data stored inside the object as well 
as the data itself. Serializing an object is a process of converting that object into a sequence of 
bytes. Deserialization is a process creating object back from the sequence of bytes. Aerospike Transcoder 
interface allows you to serialize and deserialize Java Objects. You can also implement your own 
serializer-deserailizer using this interface. 

This transcoder interface has two operations:
* encode(): This operation will take an object and return a byte array representation.
* decode(): This operation will take byte array and create an object from the sequence 
of bytes.

### Installation:

Gradle:

In your gradle project, add following to your dependencies:

```
compile 'com.aerospike:aerospike-transcoder:O.9'
```
Maven : 

In your maven project, add the following dependency:

```
	<dependency>
		<groupId>com.aerospike</groupId>
		<artifactId>aerospike-transcoder</artifactId>
		<version>0.9</version>
	</dependency>

```

SBT Project :

In your Play project, add the following dependency

```
libraryDependencies += "com.aerospike" % "aerospike-transcoder" % "0.9"
```

### Usage:

Suppose we have a serializable class ShoppingItem

```
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ShoppingItem implements Serializable{
	private static final long serialVersionUID = 2L;
	String id;
	String productname;
	int Quantity;
	
	public ShoppingItem() {
        this.id = null;
        this.productname = null;
        this.Quantity = 0;
    }
}
```

Now create a new ShoppingItem

```
ShoppingItem item = new ShoppingItem(i1, Xbox, 1)
```
Encode operation will encode ShoppingItem item into a sequence of bytes and return a byte array.

```
import com.aerospike.transcoder.*;

private final Transcoder transcoder;
  public static void main( String[] args ){
  	byte[] enValue = transcoder.encode(item);
  }
```
Using Decode operation, you can get back object from the sequence of bytes.

```
public object getValue(byte[] enValue){
	return transcoder.decode(enValue)
}
```

[FasterXML-jackson dataind](https://github.com/FasterXML/jackson-databind/wiki/Serialization-Features)  and [Fast Serialization](https://github.com/RuedigerMoeller/fast-serialization) are the two implementations provided by default.
