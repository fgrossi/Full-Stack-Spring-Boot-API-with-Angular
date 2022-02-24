package io.getarrays.server.service.implementation;

import io.getarrays.server.enumeration.Status;
import io.getarrays.server.model.Server;
import io.getarrays.server.repo.ServerRepo;
import io.getarrays.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;

import static io.getarrays.server.enumeration.Status.SERVER_UP;
import static io.getarrays.server.enumeration.Status.SERVER_DOWN;
import static java.lang.Boolean.TRUE;
import static org.springframework.data.domain.PageRequest.of;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServerServiceImpl implements ServerService {
    private ServerRepo serverRepo;

    @Override
    public Server create(Server server) {
        log.info("Saving new server {}", server.getName());
        server.setImageUrl(setServerImageUrl());
        return serverRepo.save(server);
    }

    @Override
    public Server ping(String ipAddress) throws IOException {
        log.info("Pinging server IP {}", ipAddress);
        Server server = serverRepo.findByIpAddress(ipAddress);
        InetAddress address = InetAddress.getByName(ipAddress);
        server.setStatus(address.isReachable(10000)  ? SERVER_UP : SERVER_DOWN);
        return serverRepo.save(server);
    }

    @Override
    public Collection<Server> list(int limit)
    {
        log.info("Fetching all servers");
        return serverRepo.findAll(of(0,limit)).toList();
    }

    @Override
    public Server get(Long id) {
        log.info("Fetching server by Id: {}", id);
        return serverRepo.findById(id).get();
    }

    @Override
    public Server update(Server server) {
        log.info("Updating server {}", server.getName());
        return serverRepo.save(server);
    }

    @Override
    public Boolean delete(Long id) {
        log.info("Deleting server by ID {}", id);
        serverRepo.deleteById(id);
        return TRUE;

    }


    private String setServerImageUrl() {
        String[] imageNames = {"server1.png", "server2.png", "server3.png", "server4.png"};
        ServletUriComponentsBuilder.fromCurrentContextPath().path("server/image/" + imageNames[new Random().nextInt(4)]).toUriString();
        return null;
    }
}
