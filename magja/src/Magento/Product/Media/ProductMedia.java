/**
 * based on source code from k5
 * http://www.magentocommerce.com/boards/viewthread/37982/
 *
 * @author Pawel Konczalski <mail@konczalski.de>
 *
 * You are free to use it under the terms of the GNU General Public License
 */

/*
 * Product Images API
 * http://www.magentocommerce.com/wiki/doc/webservices-api/api/catalog_product_attribute_media
 */
package Magento.Product.Media;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Magento.Connection;
import Magento.ResourcePath;
import Magento.Utils;

public class ProductMedia extends Connection {

	/*
	 * constructor
	 */
	public ProductMedia(String user, String pass, String url) {
		super(user, pass, url);
	}

	public String create(String productSku, String pathToImage) {
		return create(productSku, pathToImage, 0);
	}

	public String create(String productSku, String pathToImage, int exclude) {
		if (pathToImage.length() > 0) {
			// set file properties
			ProductMediaFileProperties pmfp = new ProductMediaFileProperties();
			pmfp.createImageJpeg(pathToImage);

			// set media properites
			ProductMediaProperties properties = new ProductMediaProperties();
			properties.setFile(pmfp.getProperties());
			properties.setTypes(new String[] { "thumbnail", "small_image", "image" });
			properties.setExclude(exclude);

			// create
			return create(productSku, properties);
		} else {
			return "";
		}
	}

	/**
	 * Upload new product image
	 * 
	 * @param product
	 *            SKU
	 * @param path
	 *            to image file
	 * @return image file name
	 */
	public String create(String productSku, ProductMediaProperties mmp) {
		// create product
		List<Object> newMedia = new LinkedList<Object>();
		newMedia.add(productSku);
		newMedia.add(mmp.getProperties());

		try {
			return (String) client.call(ResourcePath.ProductAttributeMediaCreate, newMedia);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return "";
	}

	/*
	 * get media info
	 */
	public String getInfo(String productSku, String imageName) {
		// create product
		List<Object> newMedia = new LinkedList<Object>();
		newMedia.add(productSku);
		newMedia.add(imageName);

		try {
			Map<String, Object> media = (Map<String, Object>) client.call(ResourcePath.ProductAttributeMediaInfo, newMedia);

			return Utils.dump(media);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return "";
	}

	/*
	 * main
	 */
	public static void main(String[] args) {
		// login
		ProductMedia pm = new ProductMedia("soap", "test123", "http://192.168.1.88/magento/index.php/api/");

		// test, create images
		String newMedia = pm.create("n2610", "/tmp/3.jpg");
		System.out.println("*** DEBUG *** createMedia:" + newMedia);

		// test, get media info
		String mediaInfo = pm.getInfo("GROM2665", newMedia);
		System.out.println("*** DEBUG *** getMediaInfo:" + mediaInfo);

		// logout
		pm.logout();
	}
}