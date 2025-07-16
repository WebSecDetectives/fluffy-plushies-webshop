# ~/.bashrc: executed by bash(1) for non-login shells.
# see /usr/share/doc/bash/examples/startup-files (in the package bash-doc)
# for examples

# If not running interactively, don't do anything
[ -z "$PS1" ] && return

# don't put duplicate lines in the history. See bash(1) for more options
export HISTCONTROL=ignoredups

# check the window size after each command and, if necessary,
# update the values of LINES and COLUMNS.
shopt -s checkwinsize

# make less more friendly for non-text input files, see lesspipe(1)
[ -x /usr/bin/lesspipe ] && eval "$(lesspipe)"

# set variable identifying the chroot you work in (used in the prompt below)
if [ -z "$debian_chroot" ] && [ -r /etc/debian_chroot ]; then
    debian_chroot=$(cat /etc/debian_chroot)
fi

# set a fancy prompt (non-color, unless we know we "want" color)
case "$TERM" in
xterm-color)
    PS1='${debian_chroot:+($debian_chroot)}\[\033[01;32m\]\u@\h\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\]\$ '
    ;;
*)
    PS1='${debian_chroot:+($debian_chroot)}\u@\h:\w\$ '
    ;;
esac

# Comment in the above and uncomment this below for a color prompt
PS1='${debian_chroot:+($debian_chroot)}\[\033[01;32m\]\u@\h\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\]\$ '

# If this is an xterm set the title to user@host:dir
case "$TERM" in
xterm*|rxvt*)
    PROMPT_COMMAND='echo -ne "\033]0;${USER}@${HOSTNAME}: ${PWD/$HOME/~}\007"'
    ;;
*)
    ;;
esac

# Alias definitions.
# You may want to put all your additions into a separate file like
# ~/.bash_aliases, instead of adding them here directly.
# See /usr/share/doc/bash-doc/examples in the bash-doc package.

if [ -f ~/.bash_aliases ]; then
    . ~/.bash_aliases
fi

# enable color support of ls and also add handy aliases
if [ "$TERM" != "dumb" ]; then
    eval "`dircolors -b`"
    alias ls='ls --color=auto'
    #alias dir='ls --color=auto --format=vertical'
    #alias vdir='ls --color=auto --format=long'
fi

# some more ls aliases
alias ll='ls -lh'
alias la='ls -Ah'
alias l='ls -lAh'
alias df='df -kTh'
alias du='du -h'
alias vi='vim ' 
alias it='mvn install'
alias i='mvn install -DskipTests=true'
alias pt='mvn package' 
alias p='mvn package -DskipTests=true'
alias svns='svn status' 
alias svnd='svn status | grep !'
alias svna='svn status | grep ?'
alias sshp='ssh -p 10' 
alias syslog='sudo tail -f -n 100 /var/log/syslog' 

# enable programmable completion features (you don't need to enable
# this, if it's already enabled in /etc/bash.bashrc and /etc/profile
# sources /etc/bash.bashrc).
if [ -f /etc/bash_completion ]; then
    . /etc/bash_completion
fi

alias gitreset='git fetch --all --prune; git checkout main; git pull;'
#alias gitforce='git commit --amend --no-edit; git push -f'
alias gitprune=delete_gone_branches
alias gitbranch=git_newbranch
alias gitlog='git log --graph --oneline --all'


#export AZDO_PERSONAL_ACCESS_TOKEN=''
#export AZDO_ORG_SERVICE_URL=''

function delete_gone_branches() {
  git branch -av | grep '\[gone\]' | while IFS= read -r line; do
      # Extract the branch name
      branch_name=$(echo $line | awk '{print $1}')

      # Force delete the branch locally
      git branch -D "$branch_name"
  done
}

function git_newbranch() {
    git stash
    gitreset
    git checkout -b "$1"
    git stash pop
}

function gitforce() {
  # Check if the repository is in a rebase or merge state
  if git rev-parse --verify HEAD >/dev/null 2>&1; then
    if git rev-parse --verify REBASE_HEAD >/dev/null 2>&1 || git rev-parse --verify MERGE_HEAD >/dev/null 2>&1; then
      echo "Error: The repository is in a rebase or merge state. Aborting."
      return 1
    fi
  else
    echo "Error: Not a git repository or no commits yet."
    return 1
  fi

  # Amend the commit and force push
  git commit --amend --no-edit
  git push -f
}

# alternative check
# if [ -d .git/rebase-merge ] || [ -d .git/rebase-apply ] || git rev-parse --verify MERGE_HEAD >/dev/null 2>&1; then
#    echo "Error: The repository is in a rebase or merge state. Aborting."
#    return 1
#  fi