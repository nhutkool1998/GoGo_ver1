package cs486.nmnhut.gogo;

public class ImageName {
    String imageName;
    String imageId;

    public ImageName() {

    }

    public ImageName(String imageId, String imageName) {
        this.imageName = imageName;
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
