# User Role
# User Role

This Ansible role manages user accounts on Linux systems, supporting both creation and removal of users.

## Features

- Create or remove user accounts
- Set user password
- Configure user groups
- Set UID/GID
- Configure SSH authorized keys
- Set up basic dotfiles (.bashrc, .vimrc)
- Configure Git settings

## Required Variables

- `user_name`: Username for the account
- `user_password`: Password hash for the user (required for creation)

## Optional Variables

- `user_state`: User state, either 'present' or 'absent' (default: 'present')
- `user_default_groups`: List of groups the user should belong to (default: ['users', 'sudo'])
- `user_public_key`: SSH public key for the user's authorized_keys file
- `user_uid`: Specific UID for the user
- `user_primary_group`: Primary group for the user (default: 'users')
- `user_shell`: Login shell for the user (default: '/bin/bash')

## Example Usage

### Create a user

```yaml
- name: Add a user
  hosts: all
  roles:
    - role: user
      user_name: john
      user_password: "$6$mysecretsalt$qJbapG68nyRab0B..." # hashed password
      user_public_key: "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAI...."
      user_default_groups: ["users", "sudo", "docker"]
```

### Remove a user

```yaml
- name: Remove a user
  hosts: all
  roles:
    - role: user
      user_name: john
      user_state: absent
```
This Ansible role creates users with consistent configuration including dotfiles and SSH keys.

## Features

- Creates users with consistent configurations
- Supports fixed UID/GID assignment for NFS compatibility
- Sets up standard bash and vim configurations
- Configures SSH authorized keys

## Role Variables

### Required Variables
