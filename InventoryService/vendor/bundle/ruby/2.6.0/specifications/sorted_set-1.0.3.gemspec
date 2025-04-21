# -*- encoding: utf-8 -*-
# stub: sorted_set 1.0.3 ruby lib

Gem::Specification.new do |s|
  s.name = "sorted_set".freeze
  s.version = "1.0.3"

  s.required_rubygems_version = Gem::Requirement.new(">= 0".freeze) if s.respond_to? :required_rubygems_version=
  s.metadata = { "changelog_uri" => "https://github.com/knu/sorted_set/blob/v1.0.3/CHANGELOG.md", "homepage_uri" => "https://github.com/knu/sorted_set", "source_code_uri" => "https://github.com/knu/sorted_set" } if s.respond_to? :metadata=
  s.require_paths = ["lib".freeze]
  s.authors = ["Akinori MUSHA".freeze]
  s.bindir = "exe".freeze
  s.date = "2021-02-13"
  s.description = "Implements a variant of Set whose elements are sorted in ascending order".freeze
  s.email = ["knu@idaemons.org".freeze]
  s.homepage = "https://github.com/knu/sorted_set".freeze
  s.licenses = ["BSD-2-Clause".freeze]
  s.required_ruby_version = Gem::Requirement.new(">= 2.3.0".freeze)
  s.rubygems_version = "3.0.3.1".freeze
  s.summary = "Implements a variant of Set whose elements are sorted in ascending order".freeze

  s.installed_by_version = "3.0.3.1" if s.respond_to? :installed_by_version

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<set>.freeze, ["~> 1.0"])
      s.add_runtime_dependency(%q<rbtree>.freeze, [">= 0"])
    else
      s.add_dependency(%q<set>.freeze, ["~> 1.0"])
      s.add_dependency(%q<rbtree>.freeze, [">= 0"])
    end
  else
    s.add_dependency(%q<set>.freeze, ["~> 1.0"])
    s.add_dependency(%q<rbtree>.freeze, [">= 0"])
  end
end
