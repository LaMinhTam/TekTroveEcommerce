package com.tektrove.tektroveadmin.product;

import com.tektrove.tektroveadmin.utils.FileUploadUtil;
import com.tektrovecommon.entity.product.Product;
import com.tektrovecommon.entity.product.ProductDetail;
import com.tektrovecommon.entity.product.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ProductSaveHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    public static void setMainImageName(MultipartFile fileImage, Product product) {
        if (!fileImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(fileImage.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    public static void setExistingExtraImage(String[] imageIDs, String[] imageNames, Product product) {
        if (imageNames == null || imageIDs.length == 0) return;
        Set<ProductImage> images = new HashSet<>();
        for (int i = 0; i < imageIDs.length; i++) {
            int id = Integer.parseInt(imageIDs[i]);
            String name = imageNames[i];
            images.add(new ProductImage(id, name, product));
        }
        product.setImages(images);
    }

    public static void setNewExtraImageNames(MultipartFile[] extraImage, Product product) {
        for (MultipartFile file : extraImage) {
            if (!file.isEmpty()) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                if (product.containsImageName(fileName)) {
                    product.addExtraImage(fileName);
                }
            }
        }
    }

    public static void saveUploadedImages(MultipartFile mainImage, MultipartFile[] extraImage, Product product) throws IOException {
        if (!mainImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImage.getOriginalFilename());
            String uploadDir = "../product-images/" + product.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImage);
        }
        if (extraImage.length > 0) {
            String uploadDir = "../product-images/" + product.getId() + "/extras";
            for (MultipartFile file : extraImage) {
                if (!file.isEmpty()) {
                    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                    FileUploadUtil.saveFile(uploadDir, fileName, file);
                }
            }
        }
    }

    public static void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
        int numberOfDetails = detailNames.length;
        for (int i = 0; i < numberOfDetails; i++) {
            int id = Integer.parseInt(detailIDs[i]);
            String name = detailNames[i];
            String value = detailValues[i];
            if (!name.isEmpty() && !value.isEmpty()) {
                ProductDetail productDetail = (id != 0 ?
                        new ProductDetail(id, name, value, product) :
                        new ProductDetail(name, value, product));
                product.addDetail(productDetail);
            }
        }
    }

    public static void deleteExTraImagesRemovedOnForm(Product product) {
        String extraImageDir = "../product-images/" + product.getId() + "/extras";
        Path dirPath = Paths.get(extraImageDir);
        try {
            Files.list(dirPath).forEach(file -> {
                String fileName = file.toFile().getName();
                if (!product.containsImageName(fileName)) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        LOGGER.error("Could not delete file: " + fileName);
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("Could not list directory: " + dirPath);
        }
    }
}
