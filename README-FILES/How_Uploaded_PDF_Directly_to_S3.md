# How did I uploaded generated PDF directly to S3 instead saving locally and then uploading to S3 ?

1. In PDFGenerator class, method generateItenerary() now returns ByteArrayInputStream after creating PDF.

2. We created object of ByteArrayOutputStream and passed document to it.

3. When complete document object is written in ByteArrayOutputStream,
   we converted ByteArrayOutputStream to ByteArrayInputStream stream.

4. This ByteArrayIntputSream is passed to S3Service to upload object.
   We created PutObjectRequest with stream parameter as below-

5. PutObjectRequest putObject = new PutObjectrequest("bucketName", "fileName", byteArrayInputStreamObject, new ObjectMetadata());

6. Lastly, pushed to object to S3 Bucket.