package faang.school.postservice.service.resource.interfaces;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.resource.implementations.ResourceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    void testGetCountByPostId() {
        long postId = 1L;
        when(resourceRepository.countByPostId(postId)).thenReturn(3);

        int result = resourceService.getCountByPostId(postId);

        assertEquals(3, result);
        verify(resourceRepository, times(1)).countByPostId(postId);
    }

    @Test
    void testSaveResource() {
        long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        Resource resource = new Resource();
        resource.setPost(post);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);

        Resource savedResource = resourceService.save(resource);

        assertNotNull(savedResource);
        assertEquals(postId, savedResource.getPost().getId());
        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    void testFindAllByPostId() {
        long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        Resource resource = new Resource();
        resource.setPost(post);
        when(resourceRepository.findAllByPostId(postId)).thenReturn(List.of(resource));

        List<Resource> resources = resourceService.findAllByPostId(postId);

        assertNotNull(resources);
        assertEquals(1, resources.size());
        assertEquals(postId, resources.get(0).getPost().getId());
        verify(resourceRepository, times(1)).findAllByPostId(postId);
    }

    @Test
    void testGetResource() {
        long resourceId = 1L;
        long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        Resource resource = new Resource();
        resource.setId(resourceId);
        resource.setPost(post);
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

        Resource foundResource = resourceService.getResource(resourceId);

        assertNotNull(foundResource);
        assertEquals(postId, resource.getPost().getId());
        assertEquals(resourceId, foundResource.getId());
        verify(resourceRepository, times(1)).findById(resourceId);
    }

    @Test
    void testGetResourceNotFound() {
        long resourceId = 1L;
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> resourceService.getResource(resourceId));
        verify(resourceRepository, times(1)).findById(resourceId);
    }

    @Test
    void testDeleteResource() {
        long resourceId = 1L;
        doNothing().when(resourceRepository).deleteById(resourceId);
        resourceService.deleteResource(resourceId);
        verify(resourceRepository, times(1)).deleteById(resourceId);
    }
}
