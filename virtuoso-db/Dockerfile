# virtuoso_db/Dockerfile
FROM openlink/virtuoso-opensource-7

# Expose necessary ports
EXPOSE 1111 8890

# CMD to run the database server manually
CMD ["/bin/bash", "-c", "virtuoso-t +wait && tail -f /dev/null"]
