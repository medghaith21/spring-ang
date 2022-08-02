package margoumi.com.margoumi.repository;

import margoumi.com.margoumi.models.FileInfo;
import margoumi.com.margoumi.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo,Long> {

    ArrayList<FileInfo> findByImage(Product product);
}
