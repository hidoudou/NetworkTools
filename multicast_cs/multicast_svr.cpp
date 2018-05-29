/* 发送端 */
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <time.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <net/if.h>

#define TRAM_STATUS_ADDR "239.1.1.30"
#define TRAM_STATUS_RECV_PORT 9200

int main(int argc, char **argv)
{
    
    struct sockaddr_in addr, mcast_addr;
    int fd, cnt;
    struct ip_mreq mreq;
    int addrlen, num;
    char msg[32];
    
    sprintf(msg, "%s", "hello");

    if ((fd = socket(AF_INET, SOCK_DGRAM, 0)) < 0)
    {
        perror("socket");
        return -1;
    }

    memset(&mcast_addr, 0, sizeof(mcast_addr));
    mcast_addr.sin_family = AF_INET;
    mcast_addr.sin_addr.s_addr = inet_addr(TRAM_STATUS_ADDR);
    mcast_addr.sin_port = htons(TRAM_STATUS_RECV_PORT);        

    if (sendto(fd, (const char *)msg, sizeof(msg), 0, (struct sockaddr *)&mcast_addr, sizeof(mcast_addr)) < 0)
    {
        perror("sendto");
        return -1;
    }
    printf("send ok!\n");

    setsockopt(fd, IPPROTO_IP, IP_DROP_MEMBERSHIP, &mcast_addr, sizeof(mcast_addr));
    close(fd);
    
    return 0;
}