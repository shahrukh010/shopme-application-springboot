package com.shopme.common.entity;

import java.beans.Transient;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.GetTemporaryLinkResult;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(unique = true, length = 256, nullable = false)
	private String name;
	@Column(unique = true, length = 256, nullable = false)
	private String alias;

	@Column(name = "short_description", length = 540, nullable = false)
	private String shortName;
	@Column(name = "full_description", length = 4096, nullable = false)
	private String fullDescription;

	@Column(name = "created_time")
	private Date createdTime;
	@Column(name = "updated_time")
	private Date updatedTime;

	private boolean enabled;
	@Column(name = "in_stock")
	private boolean inStock;

	private float cost;
	private float price;

	@Column(name = "discount_percent")
	private float discountPercent;

	private float length;
	private float width;
	private float height;
	private float weight;

	@Column(name = "main_image", nullable = false)
	private String mainImage;

	private int reviewCount;
	private float averageRating;

	public Product() {
	}

	public Product(Integer id) {
		this.id = id;
	}

	public int getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}

	public float getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(float averageRating) {
		this.averageRating = averageRating;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "categories_id")
	private Categories categories;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "brand_id")
	private Brand brand;

	// orphanRemoval = false because of if we adding new image or just updating then
	// all extra images delete if set orphanRemoval = true
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = false)
	private Set<ProductImages> images = new HashSet<>();
	// orphanRemoval=true remove duplicate value and insert only changed or newly
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ProductDetail> details = new HashSet<>();

	public Set<ProductDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<ProductDetail> details) {
		this.details = details;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getFullDescription() {
		return fullDescription;
	}

	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isInStock() {
		return inStock;
	}

	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(float discountPercent) {
		this.discountPercent = discountPercent;
	}

	@Transient
	public float getDiscountPrice() {

		if (discountPercent > 0)
			return price * ((100 - discountPercent) / 100);
		return this.price;

	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public Categories getCategories() {
		return categories;
	}

	public void setCategories(Categories categories) {
		this.categories = categories;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	@Transient
	public String getMainImagePath() {
		if (id == null || mainImage == null)
			return "images/default-user.png";

		return "/product-images/" + this.id + "/" + this.mainImage;

//		return getTemporaryLinkForDropboxFiles();
	}
//****************************************************************************************************	
//****************************************************************************************************	

	private String getTemporaryLinkForDropboxFiles() {
		String ACCESS_TOKEN = "sl.Bmo08ATvZ_wcJR7HZrjBSFDHZuuoYSlbj6QVGlgVx2KIvSTufJ4lKNgfUYSh6g_OLx3kiqOBHTsB7URbJ2oEqeP_7Jq7fJgY8Y5_xblW0Aewm1_Hq1q2BvZsZ8dF50r14n3-_z8VHsFNCv_IyEig";
		try {
			DbxRequestConfig config = new DbxRequestConfig("dropbox/shoppers");
			DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

			String dropboxFolderPath = "/product-images/";
			String link = "";

			// because of when link return to getImagePath() then after again loop is start
			// from 1
			Random random = new Random();
			int randomIndex = random.nextInt(166) + 1; // Generate a random index between 1 and 166

			String path = dropboxFolderPath + randomIndex + "/";
			String filePath = listImages(client, path);

			if (filePath != null) {
				System.out.println(filePath);
				GetTemporaryLinkResult result = client.files().getTemporaryLink(filePath);

				link = result.getLink().toString();
				if (!link.isEmpty()) {
					return link;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "images/default-user.png";
	}

	private String listImages(DbxClientV2 client, String dropboxFolderPath) throws DbxException {
		ListFolderResult result = client.files().listFolder(dropboxFolderPath);

		while (true) {
			for (Metadata metadata : result.getEntries()) {
				if (metadata instanceof FileMetadata && isImageFile(metadata.getName().toLowerCase())) {
					System.out.println("Image: " + metadata.getPathLower());
					return metadata.getPathLower();
				}
			}

			if (!result.getHasMore()) {
				break;
			}

			result = client.files().listFolderContinue(result.getCursor());
		}
		return null;
	}

	private static boolean isImageFile(String fileName) {
		return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")
				|| fileName.endsWith(".gif");
	}

//****************************************************************************************************	
//****************************************************************************************************	

	public String getMainImage() {
//		return "/images/default-user.png";
		return this.mainImage;
	}

	@Transient
	public String getURI() {

		return "/p/" + this.alias + "/";
	}

	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}

	public Set<ProductImages> getImages() {
		return images;
	}

	public void setImages(Set<ProductImages> images) {
		this.images = images;
	}

	public void addExtraImage(String imageName) {

		this.images.add(new ProductImages(imageName, this));
	}

	public void addDetail(String name, String value) {

		this.details.add(new ProductDetail(name, value, this));
	}

	@Override
	public String toString() {

		return "product name:" + this.name + "product price" + this.getPrice();

	}

}
