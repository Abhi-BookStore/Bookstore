# How did I uploaded generated PDF directly to S3 instead saving locally and then uploading to S3 ?

1. In PDFGenerator class, method generateItenerary() now returns ByteArrayInputStream after creating PDF.

2. We created object of ByteArrayOutputStream and passed document to it.

3. When complete document object is written in ByteArrayOutputStream,
   we converted ByteArrayOutputStream to ByteArrayInputStream stream.

4. This ByteArrayIntputSream is passed to S3Service to upload object.
   We created PutObjectRequest with stream parameter as below-

5. PutObjectRequest putObject = new PutObjectrequest("bucketName", "fileName", byteArrayInputStreamObject, new ObjectMetadata());

6. Lastly, pushed to object to S3 Bucket.


# How to download a file from URL

Ok, so we have url maybe a String so will pass in the URL constructor.
we have URL object now.
Now open an inputStream to download file in byte array.
For getting bytesArray we will use IOUtils library's toBytesArray() method with 'url object' as the parameter.

We have byte array and we need to write in outputStream. Using lambda expression to write data (array of byte) to outputStream.

My method in controller will be something like this

public StreamingResponseBody downloadFile(String myURL){

    InputStream is =null;

    try{
        URL url = new URL(myURL);
        is = url.openStream();
        byte[] data = IOUtils.toBytesArray(url);

    } catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (IOException e) {
        System.err.printf ("Failed while reading bytes. "+ e.getMessage());
        e.printStackTrace();
    }

// Writing that data to outputStream
    return outputStream -> {
        outputStream.write(data);
    };

}