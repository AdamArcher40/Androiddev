public class MainActivity extends AppCompatActivity {

    private Uri videoUri;
    private static final int REQUEST_CODE=101;
    private StorageReference videoRef;
//    //Firebase
//    FirebaseStorage storage;
//    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

    StorageReference storageReference=FirebaseStorage.getInstance().getReference();
    videoRef=storageReference.child("/videos/" + "/user intro.MP4");
    }
    public void upload(View view){
        if (videoUri!=null){
            UploadTask uploadTask=videoRef.putFile(videoUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(MainActivity.this, "Upload Failed: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this, "Upload Complete", Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    updateProgress(taskSnapshot);
                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, "Nothing to upload ", Toast.LENGTH_LONG).show();
        }
    }

    private void updateProgress(UploadTask.TaskSnapshot taskSnapshot) {

        long FileSize= taskSnapshot.getTotalByteCount();  /// Visible For Tests///
        long uploadBytes= taskSnapshot.getBytesTransferred();
        long progress= (100 * uploadBytes)/FileSize;
        ProgressBar progressBar=(ProgressBar)findViewById(R.id.pbar);
        progressBar.setProgress((int) progress);

    }
 public void Record(View view){
        Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent,REQUEST_CODE);
 }
 public void Download(View view){
        try{
            final File localFile=File.createTempFile("User Intro", "MP4");
            videoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this, "Downlaod Complete ", Toast.LENGTH_LONG).show();
                    final VideoView videoView= (VideoView)findViewById(R.id.video);
                    videoView.setVideoURI(Uri.fromFile(localFile));
                    videoView.start();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Downlaod Failed"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            Toast.makeText(MainActivity.this, "Failed to Create temp File" +e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
 }
 protected void onActivityRequest(int requestCode, int resultCode, Intent data){
        videoUri=data.getData();
        if (requestCode==REQUEST_CODE){
            if (resultCode==RESULT_OK){
                Toast.makeText(this, "Video Saved to :\n" + videoUri, Toast.LENGTH_LONG).show();
            }
            else if (resultCode==RESULT_CANCELED){
                Toast.makeText(this, "Video Recording Cancelled", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Failed to Record Video", Toast.LENGTH_LONG).show();

            }
        }
 }


}
